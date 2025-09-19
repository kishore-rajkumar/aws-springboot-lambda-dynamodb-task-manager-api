# 📘 API Samples

This document provides sample requests and responses for the Task Manager API.

---

## 🔹 Create Task

**Request**
```http
POST /tasks
Content-Type: application/json

{
  "title": "Write documentation",
  "description": "Add usage examples to README",
  "status": "PENDING"
}


**Response**
201 Created
Content-Type: application/json

{
  "id": "abc123",
  "title": "Write documentation",
  "description": "Add usage examples to README",
  "status": "PENDING",
  "createdAt": "2025-09-19T12:34:56Z"
}
```

## 🔹 Get All Tasks
**Request**
```http
GET /tasks

**Response**
200 OK

[
  {
    "id": "abc123",
    "title": "Write documentation",
    "status": "PENDING"
  },
  {
    "id": "def456",
    "title": "Fix CI pipeline",
    "status": "IN_PROGRESS"
  }
]
```

## 🔹 Get Tasks by Status
```http
GET /tasks?status=IN_PROGRESS

**Response**

200 OK

[
  {
    "id": "def456",
    "title": "Fix CI pipeline",
    "status": "IN_PROGRESS"
  }
]
```

## 🔹 Update Task
**Request**
```http
PUT /tasks/abc123
Content-Type: application/json

{
  "title": "Write documentation",
  "description": "Add usage examples to README",
  "status": "COMPLETED"
}

**Response**
200 OK

{
  "id": "abc123",
  "title": "Write documentation",
  "status": "COMPLETED"
}
```

## 🔹 Delete Task
**Request**
```http
DELETE /tasks/abc123

**Response**
204 No Content
```

# 🧪 Notes

- All timestamps are in ISO 8601 format
- Status values: PENDING, IN_PROGRESS, COMPLETED
- Replace abc123 with actual task IDs from your database




