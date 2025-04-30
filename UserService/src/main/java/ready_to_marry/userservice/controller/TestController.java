package ready_to_marry.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ready_to_marry.userservice.entity.user.Users;
import ready_to_marry.userservice.repository.TestRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final TestRepository testRepository;

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody Test test) {
        Users user = Users.builder()
                .name("dummy")
                .build();
        testRepository.save(user);
        return ResponseEntity.ok("dummy add ok");
    }
}
