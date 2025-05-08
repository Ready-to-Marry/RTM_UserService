package ready_to_marry.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.userservice.entity.user.Users;
import ready_to_marry.userservice.repository.TestRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final TestRepository testRepository;

    @GetMapping("/")
    public String index() {
        System.out.println("호출 완료");
        return "call good";
    }

    @PostMapping("/add")
    public ResponseEntity<String> add() {
        Users user = Users.builder()
                .name("dummy")
                .build();
        testRepository.save(user);
        return ResponseEntity.ok("dummy add ok");
    }
}
