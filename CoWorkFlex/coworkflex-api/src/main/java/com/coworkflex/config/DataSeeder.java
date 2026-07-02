package com.coworkflex.config;

import com.coworkflex.entity.*;
import com.coworkflex.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(SpaceRepository spaceRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.spaceRepository = spaceRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (spaceRepository.count() > 0) {
            log.info("Base déjà peuplée — seed ignoré.");
            return;
        }

        log.info("Peuplement de la base de données...");

        // ─── Utilisateurs ─────────────────────────────────────
        User admin = userRepository.save(User.builder()
            .fullName("Admin CoWork").email("admin@coworkflex.com")
            .password(passwordEncoder.encode("Admin2026!")).role(User.Role.ADMIN).build());

        User user1 = userRepository.save(User.builder()
            .fullName("Kouassi Jean").email("jean@gmail.com")
            .password(passwordEncoder.encode("password123")).build());

        User user2 = userRepository.save(User.builder()
            .fullName("Amani Fatou").email("fatou@gmail.com")
            .password(passwordEncoder.encode("password123")).build());

        log.info("Utilisateurs créés : {}, {}, {}", admin.getEmail(), user1.getEmail(), user2.getEmail());

        // ─── Espace 1 : Abidjan Plateau ───────────────────────
        Space space1 = new Space();
        space1.setName("CoWork Plateau");
        space1.setCity("Abidjan");
        space1.setAddress("12 Avenue Noguès, Plateau, Abidjan");
        space1.setCapacity(40);
        space1.setDescription("Espace moderne au cœur du quartier d'affaires d'Abidjan.");
        space1.setImageUrl("https://images.unsplash.com/photo-1497366216548-37526070297c?w=800");
        space1.setRating(4.8);

        space1.addDesk(Desk.builder().label("Bureau A1").type(DeskType.OPEN_SPACE).pricePerHour(new BigDecimal("2500")).amenities("WiFi,Écran,Prise").build());
        space1.addDesk(Desk.builder().label("Bureau A2").type(DeskType.OPEN_SPACE).pricePerHour(new BigDecimal("2500")).amenities("WiFi,Prise").build());
        space1.addDesk(Desk.builder().label("Bureau A3").type(DeskType.OPEN_SPACE).pricePerHour(new BigDecimal("2500")).amenities("WiFi,Prise").build());
        space1.addDesk(Desk.builder().label("Salle Einstein").type(DeskType.REUNION).pricePerHour(new BigDecimal("8000")).amenities("WiFi,Projecteur,Tableau blanc").build());
        space1.addDesk(Desk.builder().label("Salle Curie").type(DeskType.REUNION).pricePerHour(new BigDecimal("8000")).amenities("WiFi,TV 65\",Climatisation").build());
        space1.addDesk(Desk.builder().label("Box Privé 1").type(DeskType.PRIVE).pricePerHour(new BigDecimal("5000")).amenities("WiFi,Écran,Téléphone").build());
        space1.addDesk(Desk.builder().label("Box Privé 2").type(DeskType.PRIVE).pricePerHour(new BigDecimal("5000")).amenities("WiFi,Écran").build());
        spaceRepository.save(space1);

        // ─── Espace 2 : Abidjan Cocody ────────────────────────
        Space space2 = new Space();
        space2.setName("Innovation Hub Cocody");
        space2.setCity("Abidjan");
        space2.setAddress("Rue des Jardins, Cocody, Abidjan");
        space2.setCapacity(25);
        space2.setDescription("Hub dédié aux startups et freelances.");
        space2.setImageUrl("https://images.unsplash.com/photo-1556761175-b413da4baf72?w=800");
        space2.setRating(4.5);

        space2.addDesk(Desk.builder().label("Open B1").type(DeskType.OPEN_SPACE).pricePerHour(new BigDecimal("2000")).amenities("WiFi,Prise").build());
        space2.addDesk(Desk.builder().label("Open B2").type(DeskType.OPEN_SPACE).pricePerHour(new BigDecimal("2000")).amenities("WiFi,Prise").build());
        space2.addDesk(Desk.builder().label("Open B3").type(DeskType.OPEN_SPACE).pricePerHour(new BigDecimal("2000")).amenities("WiFi,Écran").build());
        space2.addDesk(Desk.builder().label("Salle Startup").type(DeskType.REUNION).pricePerHour(new BigDecimal("6000")).amenities("WiFi,Projecteur").build());
        space2.addDesk(Desk.builder().label("Bureau CEO").type(DeskType.PRIVE).pricePerHour(new BigDecimal("4500")).amenities("WiFi,Écran,Imprimante").build());
        spaceRepository.save(space2);

        // ─── Espace 3 : Dakar ─────────────────────────────────
        Space space3 = new Space();
        space3.setName("WorkSpace Dakar Center");
        space3.setCity("Dakar");
        space3.setAddress("3 Avenue Cheikh Anta Diop, Dakar");
        space3.setCapacity(60);
        space3.setDescription("Le plus grand espace de coworking de Dakar.");
        space3.setImageUrl("https://images.unsplash.com/photo-1517502884422-41eaead166d4?w=800");
        space3.setRating(4.9);

        space3.addDesk(Desk.builder().label("Open C1").type(DeskType.OPEN_SPACE).pricePerHour(new BigDecimal("3000")).amenities("WiFi,Prise").build());
        space3.addDesk(Desk.builder().label("Open C2").type(DeskType.OPEN_SPACE).pricePerHour(new BigDecimal("3000")).amenities("WiFi,Prise").build());
        space3.addDesk(Desk.builder().label("Open C3").type(DeskType.OPEN_SPACE).pricePerHour(new BigDecimal("3000")).amenities("WiFi,Écran").build());
        space3.addDesk(Desk.builder().label("Open C4").type(DeskType.OPEN_SPACE).pricePerHour(new BigDecimal("3000")).amenities("WiFi,Prise").build());
        space3.addDesk(Desk.builder().label("Salle Teranga").type(DeskType.REUNION).pricePerHour(new BigDecimal("10000")).amenities("WiFi,Projecteur 4K,Tableau blanc").build());
        space3.addDesk(Desk.builder().label("Salle Léopold").type(DeskType.REUNION).pricePerHour(new BigDecimal("9000")).amenities("WiFi,TV 75\",Climatisation").build());
        space3.addDesk(Desk.builder().label("Suite Executive").type(DeskType.PRIVE).pricePerHour(new BigDecimal("7000")).amenities("WiFi,Double écran,Imprimante").build());
        space3.addDesk(Desk.builder().label("Box Privé D1").type(DeskType.PRIVE).pricePerHour(new BigDecimal("5500")).amenities("WiFi,Écran").build());
        spaceRepository.save(space3);

        log.info("Seed terminé : 3 espaces créés.");
    }
}
