# CoWork-Flex — Frontend React

Frontend React 18 + Tailwind CSS pour l'application de réservation de postes de coworking. Consomme l'API Spring Boot 3 déjà en place.

Identité visuelle reprise de ton projet **ThetDesign** : rouge `#E8194B` / noir `#0a0a0a`, typographies **Bebas Neue** (titres) + **Inter** (texte), cartes arrondies (12px) avec effet de survol "lift", badges colorés, icônes Tabler.

## Installation

```bash
cd coworkflex-web
npm install
cp .env.example .env   # ajuste VITE_API_URL si besoin
npm run dev
```

L'app tourne sur `http://localhost:5173` et appelle par défaut `http://localhost:8080/api` (ton backend Spring Boot).

## Structure

```
src/
├── api/client.js          axios + interceptor JWT (Authorization: Bearer ...)
├── context/AuthContext.jsx login / register / logout, persistance localStorage
├── hooks/
│   ├── useSpaces.js        liste des espaces (filtres ville/capacité) + détail espace+postes
│   └── useReservations.js  historique, création, annulation
├── components/
│   ├── Navbar.jsx          header + menu mobile
│   ├── SpaceCard.jsx       carte espace (Dashboard)
│   ├── DeskCard.jsx        carte poste (badge par type OPEN_SPACE/REUNION/PRIVE)
│   ├── ReservationModal.jsx modal flou, calcul de prix en direct, gestion du conflit 409
│   ├── StatusBadge.jsx     CONFIRMED / CANCELLED
│   └── Loader.jsx
├── pages/
│   ├── Dashboard.jsx       recherche + filtres + grille d'espaces
│   ├── SpaceDetail.jsx     infos espace + grille de postes
│   ├── Profile.jsx         tableau des réservations, bouton Annuler grisé si < 24h
│   └── Login.jsx           connexion / inscription
└── utils/dateUtils.js      formatage dates/prix, règle des 24h (isCancellable)
```

## Points d'intégration avec le backend

- Le token JWT renvoyé par `POST /auth/login` est stocké dans `localStorage` (`cw_token`) et injecté automatiquement sur chaque requête.
- `POST /api/reservations` : si le backend répond **409** (chevauchement), la modale affiche "Ce poste est déjà réservé sur ce créneau."
- `DELETE /api/reservations/{id}` : le bouton "Annuler" est désactivé côté client dès que `startDateTime` est à moins de 24h (fonction `isCancellable`), en miroir de la règle métier du backend — le backend reste la source de vérité et peut renvoyer une erreur si l'appel passe quand même.
- `GET /api/spaces?city=&capacity=` : les filtres du Dashboard sont envoyés en query params.

## À adapter selon ton DTO exact

- `Reservation` retourné par `GET /reservations/user/{userId}` : le tableau du Profil lit `r.deskLabel` en priorité, puis `r.desk?.label`, sinon affiche `Poste #id`. Adapte selon ce que ton `ReservationDTO` expose réellement.
- `POST /auth/login` : le code attend `{ token, user }` ou `{ token, userId, fullName }` dans la réponse — ajuste `AuthContext.jsx` si la forme diffère.
