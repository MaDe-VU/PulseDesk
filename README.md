# PulseDesk

PulseDesk is a backend application that automatically turns user comments into support tickets using AI. It is built with Java and Spring Boot, and uses the Hugging Face Inference API.

When a comment is submitted, PulseDesk runs it through an AI classification pipeline to determine whether it represents a real support issue. If it does, a structured ticket is automatically generated and stored with a title, category, priority, and summary.

---

## How It Works

Each comment is processed through the following pipeline:

1. **Relevance check** - filters out nonsense, gibberish, or input unrelated to software
2. **Ticket check** - determines if the comment is a problem/complaint or a compliment
3. **Category classification** - classifies the ticket as `bug`, `feature`, `billing`, `account`, or `other`
4. **Priority classification** - two-step binary check:
   - Is it a critical system failure or data loss? - `high`
   - If not, is it a functional issue or purely cosmetic? - `medium` or `low`
5. **Ticket generation** - title and summary are derived from the comment text, ticket is stored in the database

---

## AI Model

PulseDesk uses `facebook/bart-large-mnli` via the Hugging Face Inference API. This is a zero-shot classification model, meaning it can classify text into categories without being specifically trained on support ticket data.

### Why bart-large-mnli?
The originally suggested models (`google/flan-t5-base`, `mistralai/Mistral-7B-Instruct`, `tiiuae/falcon-7b-instruct`) are currently only available through paid third-party inference providers via the Hugging Face router. `facebook/bart-large-mnli` was chosen as it runs entirely on Hugging Face's own free inference infrastructure and is well suited for the core classification task.

### Known Limitations
- Title and summary are derived programmatically from the comment text rather than AI-generated, as `bart-large-mnli` is a classification model and does not generate free-form text
- Priority classification is approximate for edge cases - ambiguous comments may receive a slightly higher priority than expected. This is an intentional tradeoff: false negatives (missing real issues) are considered more harmful than false positives
- The free Hugging Face tier models occasionally go cold and may take some time. If the API is unavailable, the comment is saved but no ticket is created

---

## Tech Stack

- Java 25
- Spring Boot 4
- Spring Data JPA
- H2 in-memory database
- Hugging Face Inference API (`facebook/bart-large-mnli`)

---

## Prerequisites

- Java 21 or higher installed
- Git installed
- A free Hugging Face account and API token
  - Sign up at [huggingface.co](https://huggingface.co)
  - Go to **Settings → Access Tokens → New Token**
  - Copy the token

---

## Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/MaDe-VU/PulseDesk
cd PulseDesk
```

### 2. Create your local properties file

**Windows:**
```cmd
copy src\main\resources\application-local.properties.example src\main\resources\application-local.properties
```

**Linux / macOS:**
```bash
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
```

### 3. Add your Hugging Face token

Open `src/main/resources/application-local.properties` and replace `your_token_here` with your actual token:
```properties
huggingface.api.token=your_actual_token_here
```

### 4. Run the application

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

---

## Using the UI

Open your browser and go to `http://localhost:8080` to access the PulseDesk interface. You can submit comments and view generated tickets directly from the UI.

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/comments` | Submit a new comment for analysis |
| `GET` | `/comments` | Retrieve all submitted comments |
| `GET` | `/tickets` | Retrieve all generated tickets |
| `GET` | `/tickets/{id}` | Retrieve a specific ticket by ID |

### Example request

**POST /comments**
```json
{
    "text": "The app crashes every time I try to upload a file"
}
```

**Example response:**
```json
{
    "id": 1,
    "text": "The app crashes every time I try to upload a file",
    "createdAt": "2026-03-21T12:00:00"
}
```

---

## H2 Database Console

The H2 in-memory database console is available at `http://localhost:8080/h2-console` while the application is running.

Use these credentials to connect:

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:pulsedeskdb` |
| Username | `sa` |
| Password | *(leave empty)* |

Note: Since H2 is an in-memory database, all data is lost when the application stops. This is intentional for development purposes.

---

## Running Tests

**Windows:**
```cmd
mvnw.cmd test
```

**Linux / macOS:**
```bash
./mvnw test
```

The tests include:
- **Unit tests** - core business logic tested with mocked AI responses
- **Integration tests** - full end to end flow with real Hugging Face API calls
- **Model tests** - entity class validation

Note: Integration tests require an active internet connection and a valid Hugging Face token as they make real API calls.

---

## Project Structure
```
src/main/java/com/example/pulsedesk/
├── PulsedeskApplication.java         
├── controllers/
│   ├── CommentController.java        
│   └── TicketController.java         
├── models/
│   ├── Comment.java                  
│   └── Ticket.java                   
├── repositories/
│   ├── CommentRepository.java        
│   └── TicketRepository.java         
└── services/
    ├── CommentService.java           
    ├── HuggingFaceService.java       

src/main/resources/
├── application.properties            
├── application-local.properties      
└── static/
    └── index.html                    
```
