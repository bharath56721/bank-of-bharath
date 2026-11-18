package com.bankofbharath.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = @Index(columnList = "accountNumber"))
@Getter
@Setter
public class Txn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String accountNumber;
    @Column(nullable = false, length = 2)
    private String type;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public static Txn of(String acc, String type, BigDecimal amount, String description) {
        Txn t = new Txn();
        t.accountNumber = acc;
        t.type = type;
        t.amount = amount;
        t.description = description;
        return t;
    }
}
