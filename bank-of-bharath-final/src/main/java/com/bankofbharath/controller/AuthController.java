package com.bankofbharath.controller;

import com.bankofbharath.exception.ApiException;
import com.bankofbharath.model.Txn;
import com.bankofbharath.model.User;
import com.bankofbharath.repo.TxnRepo;
import com.bankofbharath.repo.UserRepo;
import com.bankofbharath.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.security.SecureRandom;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepo users;
    private final TxnRepo txns;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;
    private final SecureRandom random = new SecureRandom();

    public record RegisterReq(@NotBlank String name, @NotBlank @Email String email,
                              @NotBlank @Pattern(regexp = "\\d{10}", message = "must be 10 digits") String phone,
                              @NotBlank @Size(min = 8, message = "must be at least 8 characters") String password,
                              @DecimalMin("0") BigDecimal deposit) {
    }

    public record LoginReq(@NotBlank String id, @NotBlank String password) {
    }

    public record AuthRes(String token, String accountNumber, String name) {
    }

    @PostMapping("/register")
    @Transactional
    public AuthRes register(@Valid @RequestBody RegisterReq r) {
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }
        BigDecimal deposit = r.deposit() == null ? BigDecimal.ZERO : r.deposit();
        User u = new User();
        u.setAccountNumber(nextAccountNumber());
        u.setName(r.name().trim());
        u.setEmail(email);
        u.setPhone(r.phone());
        u.setPassword(encoder.encode(r.password()));
        u.setBalance(deposit);
        users.save(u);
        if (deposit.signum() > 0) {
            txns.save(Txn.of(u.getAccountNumber(), "CR", deposit, "Opening deposit"));
        }
        return new AuthRes(jwt.generate(u.getAccountNumber()), u.getAccountNumber(), u.getName());
    }

    @PostMapping("/login")
    public AuthRes login(@Valid @RequestBody LoginReq r) {
        String id = r.id().trim();
        User u = users.findByAccountNumberOrEmail(id.toUpperCase(), id.toLowerCase())
                .filter(x -> encoder.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        return new AuthRes(jwt.generate(u.getAccountNumber()), u.getAccountNumber(), u.getName());
    }

    private String nextAccountNumber() {
        String acc;
        do {
            acc = "BOB" + (1_000_000_000L + (long) (random.nextDouble() * 9_000_000_000L));
        } while (users.existsByAccountNumber(acc));
        return acc;
    }
}
