Feature: Обработка ошибок API погоды

  Scenario Outline: Проверка ошибок API
    When Запрашиваем погоду для "<city>" с ключом "<apiKey>" с параметрами:
      | responseCode | errorMessageCode | errorMessage                     |
      | <responseCode> | <errorMessageCode> | <errorMessage>               |
    Then Должны получить ошибку:
      """
      {
        "code": <errorMessageCode>,
        "message": "<errorMessage>"
      }
      """

    Examples:
      | city     | apiKey      | responseCode | errorMessageCode | errorMessage                             |
      |          | valid_key   | 400          | 1003             | Parameter 'q' not provided.              |
      | Moscow   |             | 401          | 1002             | API key is invalid or not provided.      |
      | Nowhere1 | valid_key   | 400          | 1006             | No location found matching parameter 'q' |
      | Moscow   | inValid_key | 401          | 2006             | API key is invalid.                      |

