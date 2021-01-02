package za.co.hailer.aortacore.controller;

import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.hailer.aortacore.model.UploadResponse;
import za.co.hailer.aortacore.service.FilesService;
import za.co.hailer.aortacore.util.Util;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final FilesService filesService;
    public FilesController(
      FilesService filesService
    ){
        this.filesService = filesService;
    }

    @RolesAllowed("user")
    @PostMapping(value = "/read")
    public @ResponseBody UploadResponse read(
            @RequestParam("file")MultipartFile file,
            Principal principal
    ) {
        KeycloakAuthenticationToken kp = (KeycloakAuthenticationToken) principal;

        SimpleKeycloakAccount simpleKeycloakAccount = (SimpleKeycloakAccount) kp.getDetails();

        AccessToken token  = simpleKeycloakAccount.getKeycloakSecurityContext().getToken();

        return filesService.read(
                file,
                token.getPreferredUsername()
        );
    }

    @RolesAllowed("user")
    @GetMapping(value = "/")
    public @ResponseBody List<UploadResponse> getAll(Principal principal){
        return filesService.getUserUploads(Util.getUsername(principal));
    }

    @RolesAllowed("user")
    @GetMapping(value = "/total-uploads")
    public @ResponseBody int getTotalUploads(Principal principal){
        return filesService.totalUploads(Util.getUsername(principal));
    }

    @RolesAllowed("user")
    @GetMapping(value = "/uploads-for-7-days")
    public @ResponseBody int getCountUploadsFor7days(Principal principal){
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, -7);
        Date lastWeek = calendar.getTime();
        return filesService.countUploadsFrom7DaysAgo(Util.getUsername(principal), lastWeek);
    }

}
