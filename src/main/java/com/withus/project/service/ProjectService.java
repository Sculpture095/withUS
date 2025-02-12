package com.withus.project.service;

import com.withus.project.domain.dto.PageResponse;
import com.withus.project.domain.dto.projects.ProjectDTO;
import com.withus.project.domain.members.*;
import com.withus.project.domain.projects.ProjectEntity;
import com.withus.project.domain.projects.ProjectStatus;
import com.withus.project.domain.projects.SelectProjectEntity;
import com.withus.project.mapper.projects.ProjectMapper;
import com.withus.project.repository.members.MemberRepositoryImpl;
import com.withus.project.repository.projects.ProjectRepositoryImpl;
import com.withus.project.repository.projects.SelectProjectRepositoryImpl;
import com.withus.project.repository.projects.SelectSkillRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepositoryImpl projectRepository;
    private final ProjectMapper projectMapper;
    private final MemberRepositoryImpl memberRepository;
    private final SelectProjectRepositoryImpl selectProjectRepository;
    private final SelectSkillRepositoryImpl selectSkillRepository;

    // 프로젝트 생성 (Client만 가능)
    public ProjectDTO createProject(ProjectDTO dto) {
        ClientEntity client = memberRepository.findClientById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 클라이언트를 찾을 수 없습니다. ID: " + dto.getMemberId()));

        if (client.getMember().getPcaType() != PcaType.CLIENT) {
            throw new IllegalArgumentException("프로젝트를 등록할 권한이 없습니다.");
        }

        validateProjectDates(dto.getClosingDate(), dto.getStartDate());



        ProjectEntity entity = projectMapper.toEntity(dto);
        entity.setClient(client);
        ProjectEntity savedEntity = projectRepository.save(entity);
        return projectMapper.toDTO(savedEntity);
    }

    // 클라이언트가 자신의 프로젝트를 조회
    public List<ProjectDTO> getClientProjects(String id) {
        System.out.println("🔍 [ProjectService] 클라이언트 프로젝트 조회 요청: " + id);

        List<ProjectEntity> projects = projectRepository.findProjectByMemberId(id);

        if (projects.isEmpty()) {
            System.out.println("⚠ [ProjectService] 등록된 프로젝트가 없습니다.");
        } else {
            for (ProjectEntity project : projects) {
                System.out.println("📌 프로젝트 ID: " + project.getProjectId() + ", 이름: " + project.getProjectName());
            }
        }

        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // 클라이언트가 자신의 프로젝트를 수정
    public void updateClientProject(String id, String projectId, Map<String, Object> fields) {
        validateClientOwnership(id, projectId);
        projectRepository.partialUpdate(projectId, fields);
    }

    // 클라이언트가 자신의 프로젝트를 삭제
    public void deleteClientProject(String id, String projectId) {
        validateClientOwnership(id, projectId);
        projectRepository.deleteById(projectId);
    }

    // 프로젝트 상태 기반 조회
    public List<ProjectDTO> getProjectsByStatus(ProjectStatus status) {
        List<ProjectEntity> projects = projectRepository.findByStatus(status);

        // 🔹 프로젝트가 없을 경우 기본적으로 모든 프로젝트 반환
        if (projects.isEmpty()) {
            projects = projectRepository.findAllProjects();
        }

        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // 금액 순 조회
    public List<ProjectDTO> getProjectsByAmount(boolean ascending) {
        return projectRepository.findByAmount(ascending)
                .stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // 지원자 적은 순 조회
    public List<ProjectDTO> getProjectsByTeamSize() {
        return projectRepository.findByTeamSize()
                .stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // 등록일자 순 조회
    public List<ProjectDTO> getProjectsByStartDate(boolean ascending) {
        return projectRepository.findByStartDate(ascending)
                .stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // 페이징 처리된 프로젝트 조회
    public PageResponse<ProjectDTO> getPagedProjects(int page, int size) {
        long totalElements = projectRepository.count();
        List<ProjectEntity> projects = projectRepository.findPagedProjects(page, size);

        List<ProjectDTO> content = projects.stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<ProjectDTO>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    // 프로젝트 검색
    public List<ProjectDTO> searchProjects(String keyword, ProjectStatus status, Double minAmount, Double maxAmount) {
        List<ProjectEntity> projects = projectRepository.searchProjects(keyword, status, minAmount, maxAmount);
        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // 파트너가 특정 프로젝트에 지원
    @Transactional
    public void applyToProject(String projectId, String id) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. projectIdx=" + projectId));

        if (project.getProStatement() != ProjectStatus.ON_GOING) {
            throw new IllegalArgumentException("현재 프로젝트는 모집 중이 아니므로 지원할 수 없습니다.");
        }

        if (selectProjectRepository.existsByProjectIdAndMemberId(projectId, id)) {
            throw new IllegalArgumentException("이미 지원한 프로젝트입니다.");
        }

        PartnerEntity partner = memberRepository.findPartnerById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 파트너를 찾을 수 없습니다. ID: " + id));

        SelectProjectEntity selectProject = new SelectProjectEntity();
        selectProject.setProject(project);
        selectProject.setPartner(partner);
        selectProject.setYn(true);

        selectProjectRepository.save(selectProject);
    }



    // 특정 프로젝트에서 파트너 삭제
    public void removePartnerFromProject(String projectId, String id) {
        selectProjectRepository.deleteByProjectIdAndMemberId(projectId, id);
    }

    // 특정 프로젝트에 참여 중인 파트너 조회
    public List<String> getPartnersByProjectId(String projectId) {
        return selectProjectRepository.findByProjectId(projectId).stream()
                .map(sp -> sp.getPartner().getMember().getId())
                .collect(Collectors.toList());
    }

    // 클라이언트가 자신의 프로젝트에 지원한 지원자 조회
    public List<String> getApplicantsForProject(String projectId, String id) {
        validateClientOwnership(id, projectId);
        return selectProjectRepository.findByProjectId(projectId).stream()
                .map(sp -> sp.getPartner().getMember().getId())
                .collect(Collectors.toList());
    }



    // 특정 프로젝트와 파트너의 참여 정보 조회
    public SelectProjectEntity getParticipationInfo(String projectId, String id) {
        return selectProjectRepository.findByProjectIdAndMemberId(projectId, id);
    }

    // 프로젝트 완료 상태 업데이트
    @Transactional
    public void updateProjectCompletionStatus(String projectId, boolean isCompleted) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트를 찾을 수 없습니다: " + projectId));

        project.setIsCompleted(isCompleted);
        projectRepository.save(project);
    }


    // 클라이언트가 모집을 마감
    @Transactional
    public void closeRecruitment(String projectId, String id) {
        validateClientOwnership(id, projectId);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. projectIdx=" + projectId));

        if (project.getProStatement() == ProjectStatus.COMPLETED) {
            throw new IllegalArgumentException("이미 모집이 완료된 프로젝트입니다.");
        }

        project.setProStatement(ProjectStatus.COMPLETED);
        projectRepository.save(project);
    }


    // 클라이언트 여부 검증
    private void validateClient(String id) {
        ClientEntity client = memberRepository.findClientById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 클라이언트를 찾을 수 없습니다. ID: " + id));

        if (client.getMember().getPcaType() != PcaType.CLIENT) {
            throw new IllegalArgumentException("클라이언트가 아닙니다. ID: " + id);
        }
    }

    // 프로젝트 소유권 검증
    private void validateClientOwnership(String id, String projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. ID: " + projectId));

        if (!project.getClient().getMember().getId().equals(id)) {
            throw new IllegalArgumentException("프로젝트의 소유자가 아닙니다. ID: " + id);
        }
    }

    // 프로젝트 날짜 검증
    private void validateProjectDates(String closingDateStr, String startDateStr) {
        if (closingDateStr == null || startDateStr == null) {
            throw new IllegalArgumentException("모집 마감일(closingDate)과 시작일(startDate)은 필수입니다.");
        }

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate closingDate = LocalDate.parse(closingDateStr);

        if (!closingDate.isBefore(startDate)) {
            throw new IllegalArgumentException("모집 마감일은 프로젝트 시작일 이전이어야 합니다.");
        }
    }

    public List<ProjectDTO> getAllProjects() {
        List<ProjectEntity> projects = projectRepository.findAllProjects();
        return projects.stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }



    // projectIdx 기반 프로젝트 조회
    public ProjectDTO getProjectById(UUID projectId) {
        ProjectEntity project = projectRepository.findByUUID(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트를 찾을 수 없습니다. projectId=" + projectId));
        return projectMapper.toDTO(project);
    }

    //프로젝트에 특정 기술 추가
    @Transactional
    public void addSkillToProject(String projectId, SkillType skillType, String memberId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트를 찾을 수 없습니다: " + projectId));

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + memberId));

        SelectSkillEntity selectSkill = new SelectSkillEntity();
        selectSkill.setProject(project);
        selectSkill.setSkillType(skillType);

        selectSkillRepository.save(selectSkill);
    }

    //프로젝트에서 특정 기술 삭제
    @Transactional
    public void removeSkillFromProject(String projectId, SkillType skillType) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트를 찾을 수 없습니다: " + projectId));

        selectSkillRepository.deleteByProjectAndSkillType(project, skillType);
    }



}
