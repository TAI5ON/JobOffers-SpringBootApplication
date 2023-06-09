package pl.luczak.michal.joboffersapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.luczak.michal.joboffersapp.offer.dto.OfferDTO;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OfferSaveRequestToOfferDTOMapperTest {

    private OfferSaveRequestToOfferDTOMapper offerDTOMapper;
    private OfferSaveRequest offerSaveRequest;
    private OfferDTO offerDTO;

    @BeforeEach
    void setUp() {
        offerDTOMapper = new OfferSaveRequestToOfferDTOMapper();
        UUID uniqueID = UUID.randomUUID();
        String url = "testURL";
        String companyName = "testCompany";
        String jobName = "testJobName";
        String salary = "testSalary";
        offerDTO = OfferDTO.builder()
                .uniqueID(uniqueID)
                .url(url)
                .companyName(companyName)
                .jobName(jobName)
                .salary(salary)
                .build();
        offerSaveRequest = OfferSaveRequest.builder()
                .companyName(companyName)
                .jobName(jobName)
                .salary(salary)
                .url(url)
                .build();
    }

    @Test
    void fromOfferDTO() {
        OfferSaveRequest fromOfferDTO = offerDTOMapper.fromOfferDTO(offerDTO);
        assertThat(fromOfferDTO).isEqualTo(offerSaveRequest);
    }

    @Test
    void apply() {
        OfferDTO applied = offerDTOMapper.apply(offerSaveRequest);
        assertThat(applied)
                .usingRecursiveComparison()
                .ignoringFields("uniqueID")
                .isEqualTo(offerDTO);
    }
}