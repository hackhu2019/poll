package com.example.poll.model.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author huhao
 * @created 2022/10/10
 * Description UserDateAudit
 */
@MappedSuperclass
@JsonIgnoreProperties(value = {"createBy", "updateBy"}, allowGetters = true)
@Data
public abstract class UserDateAudit extends DateAudit {
    @CreatedBy
    @Column(updatable = false)
    private Long createdBy;

    @LastModifiedBy
    private Long updatedBy;
}
