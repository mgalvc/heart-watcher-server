# heart_watcher
Simple GUI application that stores data in a server used by patients and doctors. TCP is the transport protocol used.

# communication protocol
## from server

- subscribes

{
	"source" : "sensor",
	"action" : "register",
	"name" : name
}

- receive from server after subscribing

{
	"id" : generated_id,
	"message" : success or error message
}

- start sending patient's data

{
	"source" : "sensor",
	"action" : "send",
	"id" : generated_id,
	"payload" : {
		"movement" : movement,
		"heart_rate" : heart_rate,
		"pressure" : pressure
	}
}

### patients structure
```json
{
  "id" : 1,
  "name" : "John",
  "payload" : {
    "movement" : true,
    "heart_rate" : 110,
    "pressure" : [120, 80]
  }	 
}
```
- receives from server after sending data

{
	"message" : success or error message	
}

- pode pedir para desconectar

{
	"id" : id
}

- recebe do servidor após se desconectar

{
	"message" : success or error message
}

#saindo do doctor

- primeiro se registra

{
	"source" : "doctor",
	"action" : "register",
	"name" : name
}

- estrutura do registered_doctors

[
	{
		"id" : id,
		"name" : name
	}
]

- recebe do servidor após registro

{
	"id" : generated_id,
	"message" : success or error message
}

- pede os dados gerais dos clientes

{
	"source" : "doctor",
	"action" : "get_general"
}

- recebe dados gerais já ordenados do servidor 

{
	"payload" : [
		{
			"id" : id,
			"name" : name,
			"in_risk" : true or false
		},
		{
			...
		},
		...
	]
}

- pede os dados específicos de um paciente

{
	"source" : "doctor",
	"action" : "get_specifics",
	"id" : id
}

- recebe dados específicos do servidor

{
	"payload" : {
		"movement" : movement,
		"heart_rate" : heart_rate,
		"pressure" : pressure
	}
}

#lógica de risco

heartRate > 100 and resting
pressure > 120/80 and resting
pressure < 120/80 

