package com.wanderlust.controller;

import com.wanderlust.dto.PropertyRequest;
import com.wanderlust.dto.PropertyResponse;
import com.wanderlust.service.PropertyService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.wanderlust.security.JwtService;
import com.wanderlust.security.JwtAuthenticationFilter;
import static org.mockito.Mockito.never;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PropertyController.class)
@AutoConfigureMockMvc(addFilters = false)
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PropertyService propertyService;
    
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    void shouldGetPropertyById() throws Exception {

        // ---------- Arrange ----------

        PropertyResponse response =
                new PropertyResponse(
                        1L,
                        "Pune Villa",
                        "Beautiful villa in Pune",
                        "Pune",
                        5000.0,
                        4,
                        "image-url",
                        10L
                );


        when(propertyService.getPropertyById(1L))
                .thenReturn(response);


        // ---------- Act + Assert ----------

        mockMvc.perform(
                get("/api/properties/1")
        )
        .andExpect(status().isOk())

        // ApiResponse
        .andExpect(
                jsonPath("$.success")
                        .value(true)
        )

        .andExpect(
                jsonPath("$.message")
                        .value("Property fetched successfully")
        )

        // PropertyResponse
        .andExpect(
                jsonPath("$.data.id")
                        .value(1)
        )

        .andExpect(
                jsonPath("$.data.title")
                        .value("Pune Villa")
        )

        .andExpect(
                jsonPath("$.data.description")
                        .value("Beautiful villa in Pune")
        )

        .andExpect(
                jsonPath("$.data.location")
                        .value("Pune")
        )

        .andExpect(
                jsonPath("$.data.pricePerNight")
                        .value(5000.0)
        )

        .andExpect(
                jsonPath("$.data.maxGuests")
                        .value(4)
        )

        .andExpect(
                jsonPath("$.data.imageUrl")
                        .value("image-url")
        )

        .andExpect(
                jsonPath("$.data.ownerId")
                        .value(10)
        );
    }
    
    @Test
    void shouldGetAllProperties() throws Exception {

        // ---------- Arrange ----------

        PropertyResponse property1 =
                new PropertyResponse(
                        1L,
                        "Pune Villa",
                        "Beautiful villa in Pune",
                        "Pune",
                        5000.0,
                        4,
                        "image-url-1",
                        10L
                );

        PropertyResponse property2 =
                new PropertyResponse(
                        2L,
                        "Mumbai Apartment",
                        "Modern apartment in Mumbai",
                        "Mumbai",
                        7000.0,
                        3,
                        "image-url-2",
                        20L
                );

        when(propertyService.getAllProperties(
                null,
                null,
                null,
                null,
                null
        )).thenReturn(
                List.of(property1, property2)
        );


        // ---------- Act + Assert ----------

        mockMvc.perform(
                get("/api/properties")
        )
        .andExpect(status().isOk())

        // ApiResponse
        .andExpect(
                jsonPath("$.success")
                        .value(true)
        )

        .andExpect(
                jsonPath("$.message")
                        .value("Properties fetched successfully")
        )

        // List size
        .andExpect(
                jsonPath("$.data.length()")
                        .value(2)
        )

        // First property
        .andExpect(
                jsonPath("$.data[0].id")
                        .value(1)
        )

        .andExpect(
                jsonPath("$.data[0].title")
                        .value("Pune Villa")
        )

        .andExpect(
                jsonPath("$.data[0].location")
                        .value("Pune")
        )

        // Second property
        .andExpect(
                jsonPath("$.data[1].id")
                        .value(2)
        )

        .andExpect(
                jsonPath("$.data[1].title")
                        .value("Mumbai Apartment")
        )

        .andExpect(
                jsonPath("$.data[1].location")
                        .value("Mumbai")
        );
    }
    
    @Test
    void shouldGetPropertiesWithFiltersAndSorting() throws Exception {

        // ---------- Arrange ----------

        PropertyResponse property =
                new PropertyResponse(
                        1L,
                        "Pune Villa",
                        "Beautiful villa in Pune",
                        "Pune",
                        4000.0,
                        4,
                        "image-url",
                        10L
                );

        when(propertyService.getAllProperties(
                "Pune",
                2000.0,
                5000.0,
                4,
                "priceAsc"
        )).thenReturn(
                List.of(property)
        );


        // ---------- Act + Assert ----------

        mockMvc.perform(
                get("/api/properties")
                        .param("location", "Pune")
                        .param("minPrice", "2000")
                        .param("maxPrice", "5000")
                        .param("guests", "4")
                        .param("sort", "priceAsc")
        )
        .andExpect(status().isOk())

        // ApiResponse
        .andExpect(
                jsonPath("$.success")
                        .value(true)
        )

        .andExpect(
                jsonPath("$.message")
                        .value("Properties fetched successfully")
        )

        // Data
        .andExpect(
                jsonPath("$.data.length()")
                        .value(1)
        )

        .andExpect(
                jsonPath("$.data[0].id")
                        .value(1)
        )

        .andExpect(
                jsonPath("$.data[0].title")
                        .value("Pune Villa")
        )

        .andExpect(
                jsonPath("$.data[0].location")
                        .value("Pune")
        )

        .andExpect(
                jsonPath("$.data[0].pricePerNight")
                        .value(4000.0)
        )

        .andExpect(
                jsonPath("$.data[0].maxGuests")
                        .value(4)
        );


        // ---------- Verify ----------

        verify(propertyService)
                .getAllProperties(
                        "Pune",
                        2000.0,
                        5000.0,
                        4,
                        "priceAsc"
                );
    }
    
    @Test
    void shouldCreateProperty() throws Exception {

        // ---------- Arrange ----------

        PropertyRequest request = new PropertyRequest();

        request.setTitle("Pune Villa");
        request.setDescription("Beautiful villa in Pune");
        request.setLocation("Pune");
        request.setPricePerNight(5000.0);
        request.setMaxGuests(4);


        PropertyResponse response =
                new PropertyResponse(
                        1L,
                        "Pune Villa",
                        "Beautiful villa in Pune",
                        "Pune",
                        5000.0,
                        4,
                        null,
                        10L
                );


        when(propertyService.createProperty(any(PropertyRequest.class)))
                .thenReturn(response);


        // ---------- Act + Assert ----------

        mockMvc.perform(
                post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "Pune Villa",
                                "description": "Beautiful villa in Pune",
                                "location": "Pune",
                                "pricePerNight": 5000.0,
                                "maxGuests": 4
                            }
                            """)
        )
        .andExpect(status().isCreated())

        .andExpect(
                jsonPath("$.success")
                        .value(true)
        )

        .andExpect(
                jsonPath("$.message")
                        .value("Property created successfully")
        )

        .andExpect(
                jsonPath("$.data.id")
                        .value(1)
        )

        .andExpect(
                jsonPath("$.data.title")
                        .value("Pune Villa")
        )

        .andExpect(
                jsonPath("$.data.location")
                        .value("Pune")
        )

        .andExpect(
                jsonPath("$.data.pricePerNight")
                        .value(5000.0)
        )

        .andExpect(
                jsonPath("$.data.maxGuests")
                        .value(4)
        );


        // ---------- Verify ----------

        verify(propertyService)
                .createProperty(any(PropertyRequest.class));
    }
    
    @Test
    void shouldRejectInvalidPropertyRequest() throws Exception {

        // ---------- Act + Assert ----------

        mockMvc.perform(
                post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "",
                                "description": "Beautiful villa in Pune",
                                "location": "Pune",
                                "pricePerNight": 5000.0,
                                "maxGuests": 4
                            }
                            """)
        )
        .andExpect(status().isBadRequest());


        // ---------- Verify ----------

        verify(
                propertyService, never()
        ).createProperty(any(PropertyRequest.class));
    }
    
}