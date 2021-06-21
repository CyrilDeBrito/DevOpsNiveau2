package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.ExoDevopsApp;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.repository.TacheRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link TacheResource} REST controller.
 */
@SpringBootTest(classes = ExoDevopsApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class TacheResourceIT {

    private static final String DEFAULT_NOM_TACHE = "AAAAAAAAAA";
    private static final String UPDATED_NOM_TACHE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTacheMockMvc;

    private Tache tache;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createEntity(EntityManager em) {
        Tache tache = new Tache()
            .nomTache(DEFAULT_NOM_TACHE)
            .description(DEFAULT_DESCRIPTION);
        return tache;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createUpdatedEntity(EntityManager em) {
        Tache tache = new Tache()
            .nomTache(UPDATED_NOM_TACHE)
            .description(UPDATED_DESCRIPTION);
        return tache;
    }

    @BeforeEach
    public void initTest() {
        tache = createEntity(em);
    }

    @Test
    @Transactional
    public void createTache() throws Exception {
        int databaseSizeBeforeCreate = tacheRepository.findAll().size();
        // Create the Tache
        restTacheMockMvc.perform(post("/api/taches")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tache)))
            .andExpect(status().isCreated());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeCreate + 1);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getNomTache()).isEqualTo(DEFAULT_NOM_TACHE);
        assertThat(testTache.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createTacheWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tacheRepository.findAll().size();

        // Create the Tache with an existing ID
        tache.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTacheMockMvc.perform(post("/api/taches")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tache)))
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllTaches() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList
        restTacheMockMvc.perform(get("/api/taches?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tache.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomTache").value(hasItem(DEFAULT_NOM_TACHE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    @Transactional
    public void getTache() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get the tache
        restTacheMockMvc.perform(get("/api/taches/{id}", tache.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tache.getId().intValue()))
            .andExpect(jsonPath("$.nomTache").value(DEFAULT_NOM_TACHE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }
    @Test
    @Transactional
    public void getNonExistingTache() throws Exception {
        // Get the tache
        restTacheMockMvc.perform(get("/api/taches/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTache() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();

        // Update the tache
        Tache updatedTache = tacheRepository.findById(tache.getId()).get();
        // Disconnect from session so that the updates on updatedTache are not directly saved in db
        em.detach(updatedTache);
        updatedTache
            .nomTache(UPDATED_NOM_TACHE)
            .description(UPDATED_DESCRIPTION);

        restTacheMockMvc.perform(put("/api/taches")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedTache)))
            .andExpect(status().isOk());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getNomTache()).isEqualTo(UPDATED_NOM_TACHE);
        assertThat(testTache.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTacheMockMvc.perform(put("/api/taches")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tache)))
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTache() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        int databaseSizeBeforeDelete = tacheRepository.findAll().size();

        // Delete the tache
        restTacheMockMvc.perform(delete("/api/taches/{id}", tache.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
