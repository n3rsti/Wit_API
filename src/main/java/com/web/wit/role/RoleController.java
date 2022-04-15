package com.web.wit.role;

import com.web.wit.user.User;
import com.web.wit.user.UserController;
import com.web.wit.user.UserFacade;
import lombok.Data;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/roles")
public class RoleController {
    private final UserFacade userFacade;

    public RoleController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping()
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        try {
            Role createdRole = userFacade.saveRole(role);
            return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
        } catch(DataIntegrityViolationException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleForm roleForm){
        userFacade.addRoleToUser(roleForm.getUsername(), roleForm.getRoleName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Data
    class RoleForm{
        private String username;
        private String roleName;
    }

}


