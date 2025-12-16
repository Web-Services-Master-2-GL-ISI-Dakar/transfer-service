package sn.ondmoney.txe.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sn.ondmoney.txe.service.dto.UserDTO;

@FeignClient(name = "user-service", url = "${user.service.url:}")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDTO getUser(@PathVariable("id") String id);

    @GetMapping("/api/users/by-phone/{phone}")
    UserDTO getUserByPhone(@PathVariable("phone") String phone);
}
