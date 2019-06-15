# revolut-test-task

Micronaut framework based Rest Application for Account Transfer 

## General usage

You can obtain basic understanding of API in tests (ApiTests class), but it should be used in flow like that:

1. Create account (`POST /account/add`) and get its id in response
2. Get this account (`GET /account/{accountId}`)
3. Transfer money between them with (`POST /account/transfer`)

All amounts are BigDecimals, so you can safely pass any numbers there

## Running 

```bash
./gradlew clean build
java -jar build/libs/account-transfer-0.1-all.jar
```
