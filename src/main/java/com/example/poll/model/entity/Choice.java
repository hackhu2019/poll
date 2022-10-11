package com.example.poll.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author huhao
 * @created 2022/10/11
 * Description
 */
@Entity
@Table(name = "choices")
@Data
@EqualsAndHashCode
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 40)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    public Choice() {

    }

    public Choice(String text) {
        this.text = text;
    }
}
