# rawls-db-testing
Generate Rawls-like test data for DB comparison testing and use that test data to populate a MySQL DB using Slick.
  
## Generator

Arguments: outputJsonFile workspaceCount, levelCount, entitiesPerLevel, attributesPerEntity/Workspace  
  
Example output for `sbt "run generate json/simple-2.json 1 2 1 1"`:
```
[{
  "name": "WS1",
  "attributes": {
    "UUID 1": "0b7966b7-fa0b-4a9a-a742-e34933a3e16d"
  },
  "entities": [{
    "name": "WS1_1_1",
    "entityType": "Level 2 Entity",
    "attributes": {
      "UUID 1": "4e4cf575-3214-4598-9c4b-c77fea8a2bb6"
    },
    "refs": {

    }
  }, {
    "name": "WS1_1",
    "entityType": "Level 1 Entity",
    "attributes": {
      "UUID 1": "999a8b9e-4196-4ae1-8d9d-565d6f5bfdf5"
    },
    "refs": {
      "child_1": "WS1_1_1"
    }
  }]
}]
```

## Ingest
          
Argument: inputJsonFile

## Test
          
Argument: testName inputJsonFile

Valid tests are `types` and `exp`