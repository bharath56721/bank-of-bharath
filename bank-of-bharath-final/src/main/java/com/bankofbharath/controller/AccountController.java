package com.bankofbharath.controller;

import com.bankofbharath.exception.ApiException;
import com.bankofbharath.model.Txn;
import com.bankofbharath.model.User;
import com.bankofbharath.repo.TxnRepo;
import com.bankofbharath.repo.UserRepo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserRepo users;
    private final TxnRepo txns;
    private final PasswordEncoder encoder;

    public record AccountRes(String accountNumber, String name, String email, String phone, BigDecimal balance) {
    }

    public record TransferReq(@NotBlank String toAccount, @NotNull @DecimalMin("1") BigDecimal amount,
                              String remark, @NotBlank String password) {
    }

    public record ProfileReq(@NotBlank String name, @NotBlank @Email String email,
                             @NotBlank @Pattern(regexp = "\\d{10}", message = "must be 10 digits") String phone) {
    }

    public record PasswordReq(@NotBlank String currentPassword,
                              @NotBlank @Size(min = 8, message = "must be at least 8 characters") String newPassword) {
    }

    public record DeleteReq(@NotBlank String password) {
    }

    private User current(Principal p) {
        return users.findByAccountNumber(p.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Session expired"));
    }

    private AccountRes view(User u) {
        return new AccountRes(u.getAccountNumber(), u.getName(), u.getEmail(), u.getPhone(), u.getBalance());
    }

    @GetMapping("/me")
    public AccountRes me(Principal p) {
        return view(current(p));
    }

    @GetMapping("/transactions")
    public List<Txn> transactions(Principal p) {
        return txns.findTop20ByAccountNumberOrderByCreatedAtDesc(p.getName());
    }

    @PostMapping("/transfer")
    @Transactional
    public AccountRes transfer(Principal p, @Valid @RequestBody TransferReq r) {
        String from = p.getName();
        String to = r.toAccount().trim().toUpperCase();
        if (from.equals(to)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot transfer to your own account");
        }
        Map<String, User> locked = new HashMap<>();
        for (String acc : Stream.of(from, to).sorted().toList()) {
            locked.put(acc, users.lockByAccountNumber(acc).orElseThrow(() ->
                    new ApiException(HttpStatus.NOT_FOUND, acc.equals(from) ? "Account not found" : "Recipient account not found")));
        }
        User sender = locked.get(from);
        User receiver = locked.get(to);
        if (!encoder.matches(r.password(), sender.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Incorrect password");
        }
        if (sender.getBalance().compareTo(r.amount()) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }
        String remark = r.remark() == null || r.remark().isBlank() ? "" : " - " + r.remark().trim();
        sender.setBalance(sender.getBalance().subtract(r.amount()));
        receiver.setBalance(receiver.getBalance().add(r.amount()));
        txns.save(Txn.of(from, "DR", r.amount(), "Sent to " + receiver.getName() + " (" + to + ")" + remark));
        txns.save(Txn.of(to, "CR", r.amount(), "Received from " + sender.getName() + " (" + from + ")" + remark));
        return view(sender);
    }

    @PutMapping("/profile")
    @Transactional
    public AccountRes profile(Principal p, @Valid @RequestBody ProfileReq r) {
        User u = current(p);
        String email = r.email().trim().toLowerCase();
        if (users.findByEmail(email).filter(x -> !x.getId().equals(u.getId())).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already in use");
        }
        u.setName(r.name().trim());
        u.setEmail(email);
        u.setPhone(r.phone());
        return view(u);
    }

    @PutMapping("/password")
    @Transactional
    public Map<String, String> password(Principal p, @Valid @RequestBody PasswordReq r) {
        User u = current(p);
        if (!encoder.matches(r.currentPassword(), u.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        if (r.currentPassword().equals(r.newPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "New password must be different");
        }
        u.setPassword(encoder.encode(r.newPassword()));
        return Map.of("message", "Password updated");
    }

    @DeleteMapping
    @Transactional
    public Map<String, String> delete(Principal p, @Valid @RequestBody DeleteReq r) {
        User u = current(p);
        if (!encoder.matches(r.password(), u.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Incorrect password");
        }
        txns.deleteByAccountNumber(u.getAccountNumber());
        users.delete(u);
        return Map.of("message", "Account deleted");
    }
}
