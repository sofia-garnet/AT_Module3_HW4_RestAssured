# AT_Module3_HW4_RestAssured

1. Добавить питомца с невалидным ID (длиной 20 цифр). Проверить, что возвращается код 500.

2. Добавить питомца с рандомным ID, проверить код 200. С помощью PUT запроса изменить ему имя и статус. 
Проверить с помощью GET запроса, что у питомца с нашим ID - эти поля изменились.

3. Добавить пользователя, получить код 200. 
Достать добавленного пользователя GET запросом и сделать валидацию JSON SCHEMA полученного респонса. 
В прошлом ДЗ вы уже должны были создать сущность User, к этому классу и нужно привести этот респонс.

4. Создать питомца, получить код 200. Удалить питомца соответствующим запросом, получить код 200. 
Проверить что он удалён(отправить GET запрос с его ID и получить ответ о том, что питомец не найден).

5. Добавить питомца со статусом sold, получить код 200. 
Получить в GET запросе всех питомцем со статусом sold, проверить, что там присутствует питомец с нашим ID, 
проверить его имя на соответствие тому, которое мы указывали при добавлении.
