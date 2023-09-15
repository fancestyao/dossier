package study.neo.dossier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import study.neo.dossier.enumeration.Theme;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailMessage {
    private String address;
    private Theme theme;
    private Long applicationId;
    private Integer sesCode;
}
