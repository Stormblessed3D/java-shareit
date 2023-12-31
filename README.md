# java-shareit
**Общее описание сервиса** <br /> 
Сервис для шеринга вещей. Пользователи имеют возможность размещать вещи, которыми они готовы поделиться, атакже находить нужные вещи через поиск и брать их на время в аренду. <br /> 
Сервис позволяет бронировать вещь на определённые даты. Владелец вещи обязательно должен подтвердить бронирование.  На время бронирования доступ к такой вещи закрывается от других желающих. После того как вещь возвращена, у пользователя, который её арендовал, естьь возможность оставить отзыв.Если нужной вещи на сервисе нет, у пользователей есть возможность оставлять запросы на эту вещь. В ответ на запрос другие пользовали могут добавить нужную вещь.  <br /> 

**Реализована следующая функциональность**:      

***Пользователи*** <br /> 
**1. Получение списка пользователей** <br /> 

```
GET /users
```
**2. Получение пользователя по id** <br /> 

```
GET /users/{userId}
```
**3. Создание пользователя** <br /> 

```
POST /users
```
**4. Обновление пользователя** <br /> 

```
PATCH /users/{userId}
```
**5. Удаление пользователя** <br /> 

```
DELETE /users/{userId}
```
    
***Вещи*** <br /> 
**1. Получение списка вещей** <br /> 
Просмотр владельцем списка всех его вещей с указанием id, названия, описания и статуса доступности. Идентификатор владельца указывается в обязательном заголовке X-Sharer-User-Id.
```
GET /items
```
**2. Получение вещи по id** <br /> 
Просмотр информации о конкретной вещи по её идентификатору. Информацию о вещи может просмотреть любой пользователь. Идентификатор пользователя указывается в обязательном заголовке X-Sharer-User-Id.
```
GET /items/{itemId}
```
**3. Поиск вещи** <br /> 
Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст, и система ищет вещи, содержащие этот текст в названии или описании. Поиск возвращает только доступные для аренды вещи.
```
GET /items/search?text={text}
```
**4. Создание вещи** <br /> 
Пользователь, который добавляет в приложение новую вещь, будет считаться ее владельцем. Идентификатор пользователя указывается в обязательном заголовке X-Sharer-User-Id. При добавлении вещи обязательно указывается название, описание и статусу доступности (доступна ли она для аренды).
```
POST /items
```
**5. Обновление вещи** <br /> 
Редактировать вещь может только её владелец. Идентификатор владельца указывается в обязательном заголовке X-Sharer-User-Id. Изменить можно название, описание и статус доступа к аренде. 
```
PATCH /items/{itemId}
```
**6. Удаление вещи** <br /> 
Удалить вещь может только её владелец. Идентификатор владельца указывается в обязательном заголовке X-Sharer-User-Id.
```
DELETE /items/{itemId}
```

### ER diagram of ShareIt project
![ER diagram of ShareIt project](https://github.com/Stormblessed3D/java-shareit/blob/add-bookings/ER_diagram_ShareIt.png)