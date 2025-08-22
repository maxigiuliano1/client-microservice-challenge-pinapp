# client-microservice-challenge-pinapp
Microservicio con Java 21 + Spring Boot 3.5.3 para gestion y analisis basicos de clientes con enfoque event-driven (Kafka), Monitoreo (Actuator/Micrometer), validacion, mapeo DTO - Entidad (MapStruct) y buenas practicas de arquitectura y programacion.

## Stack tecnologico
* Java 21, Spring Boot 3.5.3, Maven. (gestor de proyecto)
* Spring Web, Spring Data JPA (Hibernate), MySQL (BD Relacional). (persistencia y manejo de datos)
* Spring for Apache Kafka. (se usa como broker de mensajeria para la comunicacion asincrona) 
* Lombok, MapStruct. (para generar automaticamente codigo de mapeo entre objetos)
* Bean Validations (para validar de forma declarativa los datos de entrada)
* springdoc-openapi (Swagger UI). (para la documentacion de los endpoints)
* Spring Boot Actuator + Micrometer Prometheus. (para monitoreo de metricas)
* JUnit 5, Mockito. (para tests)

## Arquitectura y diseño
El servicio fue diseñado con una arquitectura de microservicios que sigue el patron de diseño arquitectónico event-driven. La arquitectura se basa en la separacion de responsabilidades en capas para una mayor mantenibilidad y facilidad de desarrollo.
Para exponer los endpoints se uso RESTful ya que es el estandar mas usado para brindar datos en formato JSON.
### ¿Porque este patron arquitectonico?
Considero que en base al requerimiento era lo que mejor se adaptaba para integrar a su vez con sistema de mensajeria (Kafka), el event driven brinda un muy buen desacoplamiento lo que lo hace mas escalable y mantenible a largo plazo.

## Estructura del proyecto: Arquitectura en capas
El proyecto esta organizado en capas logicas para brindar la separacion de responsabilidades uno de los principios mas importantes.
* **Capa de presentacion** (controller)
* **Capa de servicio** (service)
* **Capa de dominio** (entity)
* **Capa de infraestructura** (repository, mapper, event)

### Patrones de diseño usados
* **Patron Repository**: para abstraer la capa de datos, permitiendo que la capa del servicio interactue con los datos sin conocer los detalles de la persistencia
* **Patron Builder**: para crear objetos (DTOs y Entidades)
* **Mappers (MapStruct)**: para la conversion de DTOs a Entidades y viceversa
### Principios de diseño usados
* **Principios SOLID**:
  * Principio de responsabilidad unica: Cada clase tiene una responsabilidad unica
  * Principio de abierto cerrado: El diseño permite agregar por ejemplo en la clase MetricsCalculator nuevas metricas o calculos, sin modificar el codigo existente
  * Principio de Liskov: Las interfaces permiten el uso de doubles (mocks) en los tests
  * Principio de Segregacion de interfaz: Las interfaces son especificas como por ejemplo en los repositorios y los mappers
  * Principio de inversion de dependencia: Las dependencias se inyectan a traves del contenedor de IoC (inversion de control) de spring

## ¿Como levantar el proyecto?
### Requisitos previos
* MySQL con base de datos de nombre **client_db** (corriendo localmente puerto debe ser 3306) 
  *     en mysql: create database client_db;
  *     el usuario en application.properties es: root y la password: mysql (se puede modificar a su preferencia)
* Zookeeper y Kafka (corriendo localmente los puertos deben ser 9092 para kafka y 2181 para zookeeper son los que viene por defecto en kafka)
**De todas formas si los puertos son otros se pueden configurar en el archivo application.properties**
* Para levantar zookeeper y kafka dirigirse al directorio donde se tiene instalado kafka y abrir 2 CMD de windows y pegar los siguientes comandos:
  *     Zookeeper (en la primer ventana de CMD): bin\windows\zookeeper-server-start config\zookeeper.properties
  *     Kafka (en la segunda ventana de CMD): bin\windows\kafka-server-start config\server.properties
* Abrir una tercera CMD por unica vez para crear el topico:
  *     Creacion del topico: bin\windows\kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic clients-topic

### Compilar y levantar
* Para la compilacion e instalacion de dependencias dentro del proyecto en una terminal/consola dentro del IDE que utilices escribir y ejecutar: 
  *     mvn clean install
* Para levantar dirigirse a la clase PinappApplication del proyecto y dar a iniciar o bien en la misma terminal/consola escribir y ejecutar:
  *     mvn spring-boot:run

### Probar el funcionamiento
* Dirigirse a: http://localhost:8080/swagger-ui/index.html y probar la creacion de clientes, la consulta de clientes y la consulta de metricas
* La url: http://localhost:8080/actuator/prometheus contiene las metricas de todo lo que se vaya probando.

## ¿Como ejecutar los tests?
Parar el proyecto y en la misma la terminal ejecutar el comando
*     mvn test

## Sistema de Mensajeria: Apache Kafka
El servicio utiliza un sistema de mensajeria asincrona por Apache Kafka para gestionar tareas que no requieren una respuesta inmediata del cliente. Este enfoque desacopla la logica de negocio y fomenta una arquitectura flexible, escalable y mantenible en el tiempo.
### Flujo del evento: funcionalidad que se implemento
* Creacion del cliente: cuando se invoca el endpoint Rest para crear un cliente la capa de servicio valida los datos y persiste en la base de datos.
* Emision del evento: una vez que el cliente se guarda correctamente, el ClientProducer publica un mensaje con los datos del nuevo cliente en el topico de kafka clients-topic, este proceso en asincrono, el productor no espera confirmacion del consumidor, lo que permite que la api responda de inmediato al cliente.
* Consumo del evento: un cliente ClientConsumer se suscribe al topico y consume el mensaje. La logica de negocio del consumidor se encarga de procesar este evento, persistiendo en la tabla de auditoria (client_events) donde se registra el tiempo en que se registro en el atributo consumedAt (El patron de auditoria o replicacion de datos es comun en arqui. orientada a eventos).
**Cabe aclarar que el consumer fue creado a fines educativos en el mismo proyecto pero lo ideal hubiese sido que quien consuma los datos sea otro microservicios**

## Limitaciones y escalabilidad
Si bien el proyecto cumple con los requisitos iniciales, hay areas de mejoras, por ejemplo:
* La posibilidad de integrar una base de datos no relacional en caso de que el volumen de dato sea muy alto es una buena opcion para escalar horizontalmente y tambien es un motor de busquedas rapidas.
* Integrar cache para reducir la carga de datos, almacenar en el cache los resultados de consultas frecuentes (redis por ejemplo para cache distribuido)
* Contenerizacion un dockerfile para contenerizar el servicio para que se pueda ejecutar en cualquier entorno
* Despliegue en la nube
* Mas tests adicionales
* Seguridad y autenticacion para que solo clientes autorizados puedan interactuar con api
