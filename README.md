# heart_watcher
Simple GUI application that stores data in a server used by patients and doctors. TCP is the transport protocol used.

# communication protocol

- sends to client after subscribing

{
	"id" : 0,
	"message" : "you are registered to server"
}

- sends to client after receiving data

{
	"message" : "data received"
}

#saindo do doctor

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

#lógica de risco

heartRate > 100 and resting
pressure > 120/80 and resting
pressure < 120/80 

