package com.withus.project.service;

import com.withus.project.domain.projects.*;
import com.withus.project.dto.PageResponse;
import com.withus.project.dto.projects.CompletedProjectDTO;
import com.withus.project.dto.projects.OngoingProjectDTO;
import com.withus.project.dto.projects.ProjectDTO;
import com.withus.project.domain.members.*;
import com.withus.project.mapper.projects.ProjectMapper;
import com.withus.project.repository.members.MemberRepositoryImpl;
import com.withus.project.repository.projects.ProjectRepositoryImpl;
import com.withus.project.repository.projects.SelectProjectRepositoryImpl;
import com.withus.project.repository.projects.SelectSkillRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private final EntityManager entityManager;

    @Transactional
    public ProjectDTO createProjectWithSkills(ProjectDTO dto, List<String> skillNames) {
        // 클라이언트 검증
        ClientEntity client = memberRepository.findClientById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 클라이언트를 찾을 수 없습니다. ID: " + dto.getMemberId()));
        if (client.getMember().getPcaType() != PcaType.CLIENT) {
            throw new IllegalArgumentException("프로젝트를 등록할 권한이 없습니다.");
        }
        validateProjectDates(dto.getClosingDate(), dto.getStartDate());

        // 프로젝트 저장 (project 테이블)
        ProjectEntity entity = projectMapper.toEntity(dto);
        entity.setClient(client);
        ProjectEntity savedEntity = projectRepository.save(entity);

        // 선택된 스킬 목록 처리 (selectskill 테이블에 한 줄씩 저장)
        if (skillNames != null && !skillNames.isEmpty()) {
            // 디버깅용 출력
            System.out.println(">>> 선택된 기술 스택: " + skillNames);
            skillNames.stream()
                    .filter(skillName -> skillName != null
                            && !skillName.trim().isEmpty()
                            && !"null".equalsIgnoreCase(skillName.trim()))
                    .forEach(skillName -> {
                        System.out.println(">>> skillName = " + skillName);
                        SkillType skillType = SkillType.fromName(skillName);
                        // savedEntity.getProjectId()는 UUID이므로 toString()으로 변환
                        addSkillToProject(savedEntity.getProjectId().toString(), skillType, dto.getMemberId());
                    });
        }
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
    public PageResponse<ProjectDTO> getAllProjectsDTO(int page, int size) {
        int offset = page * size;
        // 1) 엔티티 페이징 조회
        List<ProjectEntity> entities = projectRepository.findAllProjects(offset, size);
        long totalElements = projectRepository.countAllPartners();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // 2) 엔티티 → DTO 변환
        List<ProjectDTO> content = entities.stream()
                .map(projectMapper::toDTO) // 여기서 proStatementDescription 등 DTO 필드를 세팅
                .collect(Collectors.toList());

        return PageResponse.<ProjectDTO>builder()
                .content(content)
                .page(page)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }
    public List<ProjectDTO> getAllProjectsDTO() {
        // ProjectEntity → ProjectDTO 변환
        return projectRepository.findAllProjects()
                .stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }


    // 프로젝트 검색
    public List<ProjectDTO> searchProjects(String keyword, ProjectStatus status, Double minAmount, Double maxAmount) {
        List<ProjectEntity> projects = projectRepository.searchProjects(keyword, status, minAmount, maxAmount);
        return projects.stream().map(projectMapper::toDTO).collect(Collectors.toList());
    }

    // 파트너가 특정 프로젝트에 지원
    @Transactional
    public void applyToProject(String projectIdString, String memberId) {
        // 여기서 직접 UUID 변환 or findByUUIDToString(...) 사용
        ProjectEntity project = projectRepository.findByUUIDToString(projectIdString)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. projectId=" + projectIdString));

        if (project.getProStatement() != ProjectStatus.ON_GOING) {
            throw new IllegalArgumentException("현재 프로젝트는 모집 중이 아니므로 지원할 수 없습니다.");
        }
        if (selectProjectRepository.existsByProjectIdAndMemberId(projectIdString, memberId)) {
            throw new IllegalArgumentException("이미 지원한 프로젝트입니다.");
        }

        PartnerEntity partner = memberRepository.findPartnerById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 파트너를 찾을 수 없습니다. ID: " + memberId));

        SelectProjectEntity selectProject = new SelectProjectEntity();
        selectProject.setProject(project);
        selectProject.setPartner(partner);

        selectProjectRepository.save(selectProject);
    }


    // 파트너가 자신이 지원한 프로젝트 조회
    public List<ProjectDTO> getAppliedProjectsByPartner(String partnerId) {
        List<SelectProjectEntity> selectedProjects = selectProjectRepository.findByMemberId(partnerId);

        return selectedProjects.stream()
                .map(sp -> projectMapper.toDTO(sp.getProject())) // 프로젝트 엔티티 -> DTO 변환
                .collect(Collectors.toList());
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
    public List<String> getApplicantsForProject(String projectIdString, String clientId) {
        // 1) 소유권 검증
        validateClientOwnership(clientId, projectIdString);

        // 2) Repository 이용 -> 2단계 조회
        List<SelectProjectEntity> spList = selectProjectRepository.findByProjectId(projectIdString);

        // 3) 지원자의 member.id 목록 추출
        return spList.stream()
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
    private void validateClientOwnership(String clientId, String projectUuidString) {
        // 1) UUID 변환 후 ProjectEntity 조회
        UUID uuid = UUID.fromString(projectUuidString);
        ProjectEntity project = entityManager.createQuery(
                        "SELECT p FROM ProjectEntity p WHERE p.projectId = :uuid",
                        ProjectEntity.class
                )
                .setParameter("uuid", uuid)
                .getSingleResult();

        // 2) 소유자 확인
        if (!project.getClient().getMember().getId().equals(clientId)) {
            throw new IllegalArgumentException("프로젝트의 소유자가 아닙니다. ID: " + clientId);
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

    // 프로젝트에 특정 기술 추가 (selectskill 테이블에 한 줄씩 삽입)
    public void addSkillToProject(String projectId, SkillType skillType, String memberId) {
        ProjectEntity project = projectRepository.findByUUIDToString(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트를 찾을 수 없습니다: " + projectId));
        // MemberEntity 조회 (필요한 경우)
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

    public List<ProjectDTO> getRegisteredProjects(String clientMemberId) {
        // DB에서 해당 클라이언트의 모든 프로젝트 조회
        // -> 아직 isCompleted = false 인 것만 필터링
        List<ProjectEntity> allProjects = projectRepository.findProjectByMemberId(clientMemberId);
        // 위 메서드: ProjectRepositoryImpl.findProjectByMemberId(String id) 사용

        return allProjects.stream()
                .filter(p -> !p.getIsCompleted())  // 완료되지 않은
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * (2) 특정 클라이언트가 등록한 프로젝트 중, 완료된(isCompleted=true) 목록
     */
    public List<ProjectDTO> getCompletedProjects(String clientMemberId) {
        // 방법 A) 마찬가지로 모든 프로젝트 조회 후, isCompleted = true 필터
        List<ProjectEntity> allProjects = projectRepository.findProjectByMemberId(clientMemberId);
        return allProjects.stream()
                .filter(ProjectEntity::getIsCompleted)
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());


    }


    @Transactional(readOnly = true)
    public List<OngoingProjectDTO> getOngoingProjectDetailed(String memberId){
        String jpql = """
    SELECT c
    FROM ContractEntity c
    JOIN c.project p
    WHERE p.client.member.id = :memberId
      AND c.status = com.withus.project.domain.projects.ContractStatus.SIGNED
      AND p.isCompleted = false
    """;

        List<ContractEntity> contractList = entityManager.createQuery(jpql, ContractEntity.class)
                .setParameter("memberId", memberId)
                .getResultList();

        // ContractEntity마다 -> OngoingProjectDTO로 변환
        List<OngoingProjectDTO> result = contractList.stream()
                .map(c -> {
                    ProjectEntity p = c.getProject();

                    return OngoingProjectDTO.builder()
                            //프로젝트 정보
                            .projectId(p.getProjectId().toString())
                            .projectName(p.getProjectName())
                            .projectAmount(p.getAmount())

                            //계약정보
                            .contractId(c.getContractId().toString())
                            .contractAmount(c.getAmount())
                            .status(c.getStatus().name())
                            .progressStatus(p.getProgressStatus().name())
                            .build();
                })
                .collect(Collectors.toList());

        return result;
    }

    @Transactional
    public void chooseApplicantsAndSetMeeting(
            String clientId,
            String projectId,
            List<String> applicantIds, //선택한 파트너들의 member.id
            String meetingDateStr,
            String meetingTimeStr
    ){
        //프로젝트 소유권 검증
        validateClientOwnership(clientId, projectId);

        //projectEntity 조회
        ProjectEntity project = projectRepository.findByUUIDToString(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트가 없습니다 : "+ projectId));

        //applicantIds 각각에 대해 SelectProjectEntity 조회 -> yn=1, 미팅 일시 설정
        LocalDate date = LocalDate.parse(meetingDateStr);
        LocalTime time = LocalTime.parse(meetingTimeStr);

        for(String partnerId : applicantIds){
            SelectProjectEntity sp = selectProjectRepository.findByProjectIdAndMemberId(projectId,partnerId);
            sp.setYn(true);
            sp.setMeetingDate(date);
            sp.setMeetingTime(time);
            selectProjectRepository.save(sp);
        }

        projectRepository.save(project);
    }

    @Transactional
    public void endProject(String clientId, String projectId) {
        // 1) 클라이언트 소유 검증
        validateClientOwnership(clientId, projectId);

        // 2) project 조회
        ProjectEntity project = projectRepository.findByUUIDToString(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트를 찾을 수 없습니다: " + projectId));

        // 3) is_completed = true 설정
        project.setIsCompleted(true);
        projectRepository.save(project);
    }


    public List<CompletedProjectDTO> getCompletedProjectsWithContract(String clientMemberId) {
        return projectRepository.findCompletedProjectsWithContract(clientMemberId);
    }




}
