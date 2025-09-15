# AWS Spring Boot Lambda DynamoDB Task Manager API

This project is a Task Manager API built using Spring Boot and AWS Lambda, with DynamoDB as the persistent storage layer. It demonstrates a serverless architecture on AWS, leveraging the scalability and cost-effectiveness of Lambda functions while providing a robust API for managing tasks.

## Features

- **Serverless API:** Powered by AWS Lambda using Spring Boot for rapid development and deployment.
- **CRUD Operations:** Create, read, update, and delete tasks.
- **DynamoDB Integration:** Persistent and highly available data storage.
- **RESTful Endpoints:** Easy-to-use REST API structure.
- **Scalable Architecture:** Designed to scale automatically with AWS Lambda and DynamoDB.
- **Easy Deployment:** Integrates with AWS SAM/Serverless Framework for streamlined deployments.
- **LocalStack Integration:** Run and test AWS services locally for development and CI environments.

## Getting Started

### Prerequisites

- Java 11+ (recommended)
- Maven
- AWS CLI configured with appropriate permissions
- AWS account with DynamoDB and Lambda access
- [LocalStack](https://github.com/localstack/localstack) for local AWS service emulation

### Local Development

1. **Clone the repository:**
   ```bash
   git clone https://github.com/kishore-rajkumar/aws-springboot-lambda-dynamodb-task-manager-api.git
   cd aws-springboot-lambda-dynamodb-task-manager-api
   ```

2. **Build the project:**
   ```bash
   mvn clean package
   ```

3. **Run locally:**
   ```bash
   mvn spring-boot:run
   ```

### Local Testing with LocalStack

This project supports local testing using [LocalStack](https://github.com/localstack/localstack), an open-source tool that emulates AWS services on your local machine. This enables fast, cost-effective, and reliable development by running your infrastructure locally.

#### How to Use LocalStack

1. **Install LocalStack:**
   ```bash
   pip install localstack
   ```
   or use Docker:
   ```bash
   docker run --rm -it -p 4566:4566 -p 4571:4571 localstack/localstack
   ```

2. **Start LocalStack:**
   ```bash
   localstack start
   ```

3. **Configure AWS CLI to use LocalStack endpoints:**
   Example for DynamoDB:
   ```bash
   aws --endpoint-url=http://localhost:4566 dynamodb list-tables
   ```

4. **Run integration tests:**
   - The project includes test configurations that use LocalStack endpoints for AWS services.
   - Make sure your test environment variables are set to point at LocalStack (e.g., `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`, and custom endpoints).

5. **Benefits:**
   - No need for real AWS resources for development or CI/CD.
   - Rapid feedback, cost savings, and improved reproducibility.

### Deploying to AWS

This project is designed to be deployed as a Lambda function. Use AWS SAM or the Serverless Framework.

#### Using AWS SAM

1. **Install AWS SAM CLI:**  
   [AWS SAM Installation Guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)

2. **Build and deploy:**
   ```bash
   sam build
   sam deploy --guided
   ```

#### Using Serverless Framework

1. **Install Serverless Framework:**  
   `npm install -g serverless`

2. **Deploy:**
   ```bash
   serverless deploy
   ```

## API Endpoints

| Method | Endpoint        | Description             |
|--------|----------------|------------------------|
| GET    | `/tasks`       | List all tasks         |
| POST   | `/tasks`       | Create a new task      |
| GET    | `/tasks/{id}`  | Get a task by ID       |
| PUT    | `/tasks/{id}`  | Update a task by ID    |
| DELETE | `/tasks/{id}`  | Delete a task by ID    |

### Example Task Object

```json
{
  "id": "123",
  "title": "Buy groceries",
  "description": "Milk, Bread, Eggs",
  "status": "PENDING"
}
```

## DynamoDB Table

- **Table Name:** `Tasks`
- **Primary Key:** `id` (String)

## Technologies Used

- AWS Lambda
- AWS DynamoDB
- Spring Boot
- Maven
- AWS SAM / Serverless Framework
- LocalStack (for local AWS service emulation)

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License.

## Author

- [Kishore Rajkumar](https://github.com/kishore-rajkumar)

## References

- [Spring Cloud Function AWS Adapter](https://docs.spring.io/spring-cloud-function/docs/current/reference/html/aws.html)
- [AWS Lambda Java Documentation](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html)
- [AWS DynamoDB Documentation](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [LocalStack Documentation](https://docs.localstack.cloud/)
