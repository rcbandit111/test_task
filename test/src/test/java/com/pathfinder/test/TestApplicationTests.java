package com.pathfinder.test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RequiredArgsConstructor
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TestApplication.class)
@AutoConfigureMockMvc
class TestApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void given_origin_destination_with_Status200()
            throws Exception {

        mvc.perform(get("/routing/CZE/ITA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("route.[0]").value("CZE"))
                .andExpect(jsonPath("route.[1]").value("AUT"))
                .andExpect(jsonPath("route.[2]").value("ITA"));
    }

    @Test
    public void given_origin_destination_with_no_route_Status400()
            throws Exception {

        mvc.perform(get("/routing/PAK/USA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void given_origin_invalid_destination_with_Status400()
            throws Exception {

        mvc.perform(get("/routing/PAK/UAE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
