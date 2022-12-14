package com.example.poll.model.entity;

import com.example.poll.model.audit.DateAudit;
import lombok.Data;

import javax.persistence.*;

/**
 * @author huhao
 * @created 2022/10/11
 * Description Vote
 */
@Data
@Entity
@Table(name = "votes")
public class Vote extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "choice_id", nullable = false)
    private Choice choice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
