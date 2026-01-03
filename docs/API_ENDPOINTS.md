# Transaction Engine Service - API Endpoints

> Documentation des endpoints REST pour le service de transactions

**Base URL (via Gateway):** `http://localhost:8080`  
**Service Direct:** `http://localhost:8091`

---

## Table des matières

1. [API v1 - Transferts P2P (Recommandé)](#1-api-v1---transferts-p2p-recommandé)
2. [API v1 - Wallet](#2-api-v1---wallet)
3. [Wallets (CRUD Admin)](#3-wallets-crud-admin)
4. [Transfers (CRUD Admin)](#4-transfers-crud-admin)
5. [Transactions (Journal Admin)](#5-transactions-journal-admin)
6. [Enums & Types](#6-enums--types)

---

## 1. API v1 - Transferts P2P (Recommandé)

> API pour les transferts d'argent entre utilisateurs. L'historique est automatiquement enregistré via Kafka.

### 1.1 Effectuer un transfert P2P

```
POST /api/v1/transactions/transfer
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
X-Correlation-ID: corr_xxx (optionnel)
```

**Request Body:**
```json
{
  "receiverPhone": "+221778889999",
  "amount": 5000.00,
  "description": "Remboursement déjeuner",
  "pin": "1234"
}
```

**Response (200 OK):**
```json
{
  "transactionId": "TXN_A1B2C3D4E5F6",
  "status": "COMPLETED",
  "amount": 5000.00,
  "fees": 50.00,
  "totalDebited": 5050.00,
  "senderPhone": "+221773031545",
  "receiverPhone": "+221778889999",
  "currency": "XOF",
  "newBalance": 44950.00,
  "description": "Remboursement déjeuner",
  "initiatedAt": "2026-01-03T14:00:00Z",
  "completedAt": "2026-01-03T14:00:02Z",
  "message": "Transfert effectué avec succès"
}
```

**Codes d'erreur spécifiques:**
| Code | Erreur | Description |
|------|--------|-------------|
| 400 | `SELF_TRANSFER` | Impossible de transférer à soi-même |
| 400 | `SENDER_NOT_FOUND` | Wallet expéditeur non trouvé |
| 400 | `RECEIVER_NOT_FOUND` | Destinataire non trouvé |
| 400 | `SENDER_WALLET_INACTIVE` | Wallet expéditeur inactif |
| 400 | `RECEIVER_WALLET_INACTIVE` | Wallet destinataire inactif |
| 400 | `INSUFFICIENT_BALANCE` | Solde insuffisant |

**Frais de transfert:**
- 1% du montant
- Minimum: 25 XOF
- Maximum: 5000 XOF

---

## 2. API v1 - Wallet

### 2.1 Consulter le solde

```
GET /api/v1/wallet/balance
```

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "walletId": "wal_123",
  "phoneNumber": "+221773031545",
  "balance": 50000.00,
  "availableBalance": 50000.00,
  "currency": "XOF",
  "status": "ACTIVE",
  "lastUpdated": "2026-01-03T12:30:00Z"
}
```

---

### 2.2 Détails du wallet

```
GET /api/v1/wallet
```

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "walletId": "wal_123",
  "phoneNumber": "+221773031545",
  "balance": 50000.00,
  "availableBalance": 50000.00,
  "currency": "XOF",
  "status": "ACTIVE",
  "lastUpdated": "2026-01-03T12:30:00Z"
}
```

---

## 3. Wallets (CRUD Admin)

> Endpoints d'administration pour la gestion des wallets

---

### 3.1 Créer un portefeuille

```
POST /api/wallets
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": "keycloak-user-id",
  "phone": "+221773031545",
  "status": "ACTIVE",
  "balance": 0,
  "version": 1,
  "createdAt": "2026-01-03T10:00:00Z"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "userId": "keycloak-user-id",
  "phone": "+221773031545",
  "status": "ACTIVE",
  "balance": 0,
  "version": 1,
  "createdAt": "2026-01-03T10:00:00Z",
  "updatedAt": null
}
```

---

### 3.2 Obtenir tous les portefeuilles

```
GET /api/wallets?page=0&size=20&sort=createdAt,desc
```

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
| Paramètre | Type | Description |
|-----------|------|-------------|
| `page` | int | Numéro de page (0-indexed) |
| `size` | int | Taille de la page |
| `sort` | string | Champ de tri et direction |

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": "keycloak-user-id",
    "phone": "+221773031545",
    "status": "ACTIVE",
    "balance": 50000.00,
    "version": 3,
    "createdAt": "2026-01-03T10:00:00Z",
    "updatedAt": "2026-01-03T12:30:00Z"
  }
]
```

**Headers de pagination:**
```
X-Total-Count: 100
Link: <...>; rel="next", <...>; rel="last"
```

---

### 3.3 Obtenir un portefeuille par ID

```
GET /api/wallets/{id}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": "keycloak-user-id",
  "phone": "+221773031545",
  "status": "ACTIVE",
  "balance": 50000.00,
  "version": 3,
  "createdAt": "2026-01-03T10:00:00Z",
  "updatedAt": "2026-01-03T12:30:00Z"
}
```

---

### 3.4 Mettre à jour un portefeuille

```
PUT /api/wallets/{id}
```

**Request Body:**
```json
{
  "id": 1,
  "userId": "keycloak-user-id",
  "phone": "+221773031545",
  "status": "SUSPENDED",
  "balance": 50000.00,
  "version": 3,
  "createdAt": "2026-01-03T10:00:00Z"
}
```

---

### 3.5 Mise à jour partielle

```
PATCH /api/wallets/{id}
Content-Type: application/merge-patch+json
```

**Request Body:**
```json
{
  "id": 1,
  "status": "ACTIVE"
}
```

---

### 3.6 Supprimer un portefeuille

```
DELETE /api/wallets/{id}
```

**Response:** `204 No Content`

---

## 4. Transfers (CRUD Admin)

> Endpoints d'administration pour la gestion des transferts

### 4.1 Initier un transfert (Admin)

```
POST /api/transfers
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "txId": "TX-2026-001",
  "status": "PENDING",
  "amount": 5000.00,
  "fees": 50.00,
  "senderPhone": "+221773031545",
  "receiverPhone": "+221778889999",
  "initiatedAt": "2026-01-03T14:00:00Z"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "txId": "TX-2026-001",
  "status": "PENDING",
  "amount": 5000.00,
  "fees": 50.00,
  "senderPhone": "+221773031545",
  "receiverPhone": "+221778889999",
  "initiatedAt": "2026-01-03T14:00:00Z",
  "completedAt": null,
  "failedAt": null,
  "errorMessage": null,
  "sender": {
    "id": 1,
    "phone": "+221773031545",
    "balance": 45000.00
  },
  "receiver": {
    "id": 2,
    "phone": "+221778889999",
    "balance": 5000.00
  }
}
```

---

### 4.2 Lister tous les transferts

```
GET /api/transfers?page=0&size=20
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "txId": "TX-2026-001",
    "status": "COMPLETED",
    "amount": 5000.00,
    "fees": 50.00,
    "senderPhone": "+221773031545",
    "receiverPhone": "+221778889999",
    "initiatedAt": "2026-01-03T14:00:00Z",
    "completedAt": "2026-01-03T14:00:05Z",
    "failedAt": null,
    "errorMessage": null
  }
]
```

---

### 4.3 Obtenir un transfert par ID

```
GET /api/transfers/{id}
```

---

### 4.4 Mettre à jour un transfert

```
PUT /api/transfers/{id}
```

---

### 4.5 Supprimer un transfert

```
DELETE /api/transfers/{id}
```

---

## 5. Transactions (Journal Admin)

> Journal de tous les mouvements de solde (débits/crédits) - Administration

### 5.1 Créer une transaction

```
POST /api/transactions
```

**Request Body:**
```json
{
  "txId": "TX-2026-001",
  "externalTxId": "EXT-123456",
  "type": "TRANSFER_P2P",
  "status": "PENDING",
  "amount": 5000.00,
  "fees": 50.00,
  "source": "+221773031545",
  "destination": "+221778889999",
  "initiatedAt": "2026-01-03T14:00:00Z"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "txId": "TX-2026-001",
  "externalTxId": "EXT-123456",
  "type": "TRANSFER_P2P",
  "status": "PENDING",
  "amount": 5000.00,
  "fees": 50.00,
  "source": "+221773031545",
  "destination": "+221778889999",
  "initiatedAt": "2026-01-03T14:00:00Z",
  "completedAt": null,
  "failedAt": null,
  "errorMessage": null,
  "debitedAccount": {...},
  "creditedAccount": {...}
}
```

---

### 5.2 Lister toutes les transactions

```
GET /api/transactions?page=0&size=20&sort=initiatedAt,desc
```

---

### 5.3 Obtenir une transaction par ID

```
GET /api/transactions/{id}
```

---

### 5.4 Mettre à jour une transaction

```
PUT /api/transactions/{id}
```

---

### 5.5 Supprimer une transaction

```
DELETE /api/transactions/{id}
```

---

## 6. Enums & Types

### WalletStatus
| Valeur | Description |
|--------|-------------|
| `ACTIVE` | Portefeuille actif |
| `SUSPENDED` | Portefeuille suspendu |
| `CLOSED` | Portefeuille fermé |

### TransactionStatus
| Valeur | Description |
|--------|-------------|
| `PENDING` | Transaction en cours |
| `COMPLETED` | Transaction réussie |
| `FAILED` | Transaction échouée |

### TransactionType
| Valeur | Description |
|--------|-------------|
| `TRANSFER_P2P` | Transfert de personne à personne |
| `CARD_RECHARGE` | Recharge de carte |
| `BILL_PAYMENT` | Paiement de facture |
| `MERCHANT_PAYMENT` | Paiement marchand |
| `AIRTIME_PURCHASE` | Achat de crédit téléphonique |
| `WALLET2BANK` | Transfert vers compte bancaire |
| `BANK2WALLET` | Transfert depuis compte bancaire |

---

## Codes d'erreur

| Code HTTP | Type | Description |
|-----------|------|-------------|
| 400 | `Bad Request` | Données invalides |
| 401 | `Unauthorized` | Token manquant ou invalide |
| 403 | `Forbidden` | Accès refusé |
| 404 | `Not Found` | Ressource non trouvée |
| 409 | `Conflict` | Conflit (doublon, version) |
| 500 | `Internal Server Error` | Erreur serveur |

---

## Exemples cURL

### Effectuer un transfert P2P (API v1 - Recommandé)
```bash
curl -X POST "http://localhost:8080/api/v1/transactions/transfer" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: corr_123456" \
  -d '{
    "receiverPhone": "+221778889999",
    "amount": 5000.00,
    "description": "Remboursement déjeuner"
  }'
```

### Consulter son solde (API v1)
```bash
curl -X GET "http://localhost:8080/api/v1/wallet/balance" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Détails du wallet (API v1)
```bash
curl -X GET "http://localhost:8080/api/v1/wallet" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Créer un transfert (Admin CRUD)
```bash
curl -X POST "http://localhost:8080/api/transfers" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "txId": "TX-2026-001",
    "status": "PENDING",
    "amount": 5000.00,
    "fees": 50.00,
    "senderPhone": "+221773031545",
    "receiverPhone": "+221778889999",
    "initiatedAt": "2026-01-03T14:00:00Z"
  }'
```

### Consulter un wallet par ID (Admin)
```bash
curl -X GET "http://localhost:8080/api/wallets/1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Flux d'événements Kafka

Lors d'un transfert P2P via `/api/v1/transactions/transfer`, les événements suivants sont publiés :

1. **transfer.initiated** - Au démarrage du transfert
2. **transfer.completed** - Si le transfert réussit
3. **transfer.failed** - Si le transfert échoue

Ces événements sont consommés par `transaction-history-service` pour enregistrer automatiquement l'historique des transactions.
