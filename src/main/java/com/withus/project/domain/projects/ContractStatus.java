package com.withus.project.domain.projects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContractStatus {
    PENDING(0,"대기중"), //대기중
    SIGNED(1,"계약 완료"), //계약 완료
    CANCELED(2,"계약 취소"); //계약 취소

    private final int code;
    private final String description;

    public static ContractStatus fromCode(int code){
        for(ContractStatus status : ContractStatus.values()){
            if(status.getCode() == code){
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code for ContractStatus: " + code);
    }

    public static ContractStatus fromName(String name) {
        for (ContractStatus contractStatus : values()) {
            if (contractStatus.name().equalsIgnoreCase(name)) {
                return contractStatus;
            }
        }
        throw new IllegalArgumentException("Invalid ContractStatus: " + name);
    }
}
