# JSON Examples

## 1. Simple object
```json
{
  "name": "Ivan",
  "age": 25
}
```

## 2. Different data types
```json
{
  "name": "Maria",
  "age": 30,
  "isStudent": false,
  "height": 1.68,
  "nickname": null
}
```

## 3. With array
```json
{
  "name": "Georgi",
  "skills": ["Java", "Spring", "SQL"]
}
```

## 4. Nested object
```json
{
  "user": {
    "name": "Ivan",
    "address": {
      "city": "Sofia",
      "zip": "1000"
    }
  }
}
```

## 5. Array of objects
```json
[
  {
    "id": 1,
    "name": "Item 1"
  },
  {
    "id": 2,
    "name": "Item 2"
  }
]
```

## 6. Realistic example
```json
{
  "id": 101,
  "customer": {
    "name": "Ivan Ivanov",
    "email": "ivan@example.com"
  },
  "items": [
    {
      "product": "Laptop",
      "price": 1200
    },
    {
      "product": "Mouse",
      "price": 25
    }
  ],
  "paid": true
}
```