# 🚀 Deployment Guide

This guide explains how to deploy the Task Manager API using AWS SAM CLI.

## Prerequisites

- AWS CLI configured with credentials
- SAM CLI installed
- Docker (for local builds)
- AWS account with permissions to deploy Lambda, API Gateway, and DynamoDB

## Deployment Steps

1. **Build the project**
   ```bash
   sam build
   ```
2. **Deploy to AWS**
```bash
sam deploy --guided
```
3. **Provide required parameters**
- Stack name
- AWS region
- Confirm changeset
- Save configuration for future deploys

## Post-Deployment

    Note the API Gateway endpoint from the output

    Verify Lambda functions and DynamoDB table in AWS Console

## Cleanup

To delete the stack:
```bash
aws cloudformation delete-stack --stack-name your-stack-name
```
