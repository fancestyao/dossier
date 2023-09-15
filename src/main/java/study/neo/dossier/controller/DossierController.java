package study.neo.dossier.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dossier")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dossier API")
public class DossierController {
}
