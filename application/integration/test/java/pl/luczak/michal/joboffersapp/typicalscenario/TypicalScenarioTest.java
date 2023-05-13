package pl.luczak.michal.joboffersapp.typicalscenario;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pl.luczak.michal.joboffersapp.BaseIntegrationTest;
import pl.luczak.michal.joboffersapp.dto.OfferRequestDTO;
import pl.luczak.michal.joboffersapp.validation.controller.APIValidationErrorDTO;
import pl.luczak.michal.joboffersapp.ports.input.OfferFetcherPort;
import pl.luczak.michal.joboffersapp.offer.dto.OfferDTO;
import pl.luczak.michal.joboffersapp.ports.output.OfferService;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TypicalScenarioTest extends BaseIntegrationTest {
    /*
        step 1: there are no offers in external HTTP server (http://ec2-3-120-147-150.eu-central-1.compute.amazonaws.com:5057/offers)
        step 2: scheduler ran 1st time and made GET to external server and system added 0 offers to database
        step 3: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned UNAUTHORIZED(401)
        step 4: user made GET /offer with no jwt token and system returned UNAUTHORIZED(401)
        step 5: user made POST /register with username=someUser, password=somePassword and system registered user with status OK(200)
        step 6: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned OK(200) and jwttoken=AAAA.BBBB.CCC
        step 7: user made GET /offer with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 0 offers
        step 8: there are 2 new offers in external HTTP server
        step 9: scheduler ran 2nd time and made GET to external server and system added 2 new offers with ids: 1000 and 2000 to database
        step 10: user made GET /offer with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 2 offers with ids: 1000 and 2000
        step 11: user made GET /offer/9999 and system returned NOT_FOUND(404) with message “Offer with id 9999 not found”
        step 12: user made GET /offer/1000 and system returned OK(200) with offer
        step 13: there are 2 new offers in external HTTP server
        step 14: scheduler ran 3rd time and made GET to external server and system added 2 new offers with ids: 3000 and 4000 to database
        step 15: user made GET /offer with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 4 offers with ids: 1000,2000, 3000 and 4000
        step 16: user made POST /offer with header “Authorization: Bearer AAAA.BBBB.CCC” and offer and system returned CREATED(201) with saved offer
        step 17: user made GET /offer with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 5 offers
    */

    @Autowired
    private OfferFetcherPort<OfferRequestDTO> offerFetcherPort;

    @Autowired
    private OfferService offerService;

    @Test
    void typical_scenario() throws Exception {

    //step 1: there are no offers in external HTTP server (http://ec2-3-120-147-150.eu-central-1.compute.amazonaws.com:5057/offers)

        //given
        ResponseDefinitionBuilder response = WireMock.aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json")
                .withBody("[]".trim());
        wireMockServer.stubFor(
                WireMock.get("/offers").willReturn(response)
        );

    //step 2: scheduler ran 1st time and made GET to external server and system added 0 offers to database

        //when
        List<OfferDTO> offerDTOList = offerService.fetchAllOffersAndSaveAllIfNotExists();

        //then
        Assertions.assertTrue(offerDTOList.isEmpty());

//    //step 3: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned UNAUTHORIZED(401)
//
//        //when
//        ResultActions tokenBadRequestResult = mockMvc.perform(post("/token")
//                .content("""
//                        {
//                            "username": "someUser",
//                            "password": "somePassword"
//                        }
//                        """)
//                .contentType(MediaType.APPLICATION_JSON)
//        );
//
//        //then
//        tokenBadRequestResult.andExpect(status().isUnauthorized());

    //step 4: user made GET /offer with no jwt token and system returned UNAUTHORIZED(401)

        //given
        UUID uniqueID = UUID.randomUUID();

        //when
        ResultActions offerBadRequestResult = mockMvc.perform(get("/offer/dsd")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        MvcResult mvcResult = offerBadRequestResult.andExpect(status().isBadRequest()).andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        APIValidationErrorDTO result = objectMapper.readValue(json, APIValidationErrorDTO.class);
        Assertions.assertTrue(result.messages().contains("Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: dsd"));

//    //step 5: user made POST /register with username=someUser, password=somePassword and system registered user with status OK(200)
//
//        //when
//        ResultActions registerRequestOK = mockMvc.perform(post("/register")
//                .content("""
//                        {
//                            "username": "someUser",
//                            "password": "somePassword"
//                        }
//                        """)
//                .contentType(MediaType.APPLICATION_JSON)
//        );
//
//        //then
//        registerRequestOK.andExpect(status().isOk());







        ResultActions tokenBadRequestResult = mockMvc.perform(post("/token")
                .content("""
                        {
                            "username": "",
                            "password": ""
                        }
                        """)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        MvcResult testMvcResult = tokenBadRequestResult.andExpect(status().isBadRequest()).andReturn();
        String jsonTest = testMvcResult.getResponse().getContentAsString();
        APIValidationErrorDTO resultTest = objectMapper.readValue(jsonTest, APIValidationErrorDTO.class);
        Assertions.assertTrue(resultTest.messages().containsAll(List.of("Test2", "Test3")));
    }
}