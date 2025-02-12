package com.withus.project.mapper.members;

import com.withus.project.domain.dto.members.clients.ClientDTO;
import com.withus.project.domain.members.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    /**
     * ClientDTO -> ClientEntity 매핑
     * 요청 데이터를 DB 저장용 Entity로 변환
     *
     * @param dto ClientDTO (요청용 데이터)
     * @return ClientEntity (Entity 객체)
     */
    @Mappings({
            @Mapping(target = "clientIdx", ignore = true), // ID는 자동 생성
            @Mapping(target = "member.memberIdx", source = "memberIdx") // 회원 번호 매핑
    })
    ClientEntity toEntity(ClientDTO dto);

    /**
     * ClientEntity -> ClientDTO 매핑
     * DB 데이터를 응답용 DTO로 변환
     *
     * @param entity ClientEntity (DB 데이터)
     * @return ClientDTO (응답용 데이터)
     */
    @Mappings({
            @Mapping(target = "clientIdx", source = "clientIdx"), // 클라이언트 번호 매핑
            @Mapping(target = "memberIdx", source = "member.memberIdx") // 회원 번호 매핑
    })
    ClientDTO toDTO(ClientEntity entity);
}
