require: slotfilling/slotFilling.sc
  module = sys.zb-common

require: functions.js

init:
    $global.$ = {
        __noSuchProperty__: function(property) {
            return $jsapi.context()[property];
        }
    };

theme: /

    state: Start
        q!: $regex</start>
        q!: * меню *
        script:
            if ($request.data && $request.data.bcsClientInfo && $request.data.bcsClientInfo.bcsFirstName) {
                $client.name = $request.data.bcsClientInfo.bcsFirstName;
                if ($request.data.bcsClientInfo.bcsMiddlename) {
                    $client.name = $client.name + " " + $request.data.bcsClientInfo.bcsMiddlename;
                }
            } else if ($request.rawRequest.clientInfo && $request.rawRequest.clientInfo.firstName) {
                $client.name = $request.rawRequest.clientInfo.firstName;
            } else {
                $client.name = "неопознанный пользователь";
            }
            $response.replies = $response.replies || [];
            $response.replies.push(
                {
                    "type": "raw",
                    "body": {"sessionId": $.sessionId}
                }
            );
        a: Приветствую, {{$client.name}}!
        a: Что желаете протестировать на этот раз?
        buttons:
            "Перевод на оператора" -> /Operator
            "Текст с кнопками" -> /TextAndButtons
            "Стейт с вложенными стейтами" -> /MotherKids
            "Текст с картинкой" -> /ImageResponse
            "Маркдаун" -> /TestMarkdown
            "ХТМЛ" -> /TestHTML
            "Таймаут" -> /Timeout
            "Завершение сессии" -> /Timeout

    state: TextAndButtons
        a: Текст.
        buttons:
            {text: "Кнопка со ссылкой", url: "https://295628.selcdn.ru/mybroker/tariffs/tariff_plan_trader.pdf"}
            {text: "Привет", transition: "/Hello"}
            {text: "Пока", transition: "/Bye"}

    state: Timeout
        q!: таймаут
        a: Через 5 секунд сессия завершится.
        script:
            $reactions.timeout({interval: "5 seconds", targetState: "/AfterTime"})

    state: AfterTime
        a: Ваше время истекло.
        script:
            $jsapi.stopSession();

    state: MotherKids
        q!: *вложенность*
        a: Вы на первом уровне вложенности. Идем дальше?
        buttons:
            "Да" -> ./Yes
            "Нет" -> ./No

        state: Yes
            q: * (да/давай/конечно/пошли/идем) *
            a: Вы сказали "да", и теперь вы на втором уровне вложенности. Продолжим?
            buttons:
                "Да" -> ./Yes
                "Нет" -> ./No

            state: Yes
                q: * (да/давай/конечно/пошли/идем) *
                a: Вы сказали "да", и оказались на третьем уровне вложенности.

            state: No
                q: * (нет/не) *
                a: Вы сказали "нет", но оказались на третьем уровне вложенности.

        state: No
            q: * (нет/не) *
            a: Вы сказали "нет", но теперь вы на втором уровне вложенности. Продолжим?
            buttons:
                "Да" -> ./Yes
                "Нет" -> ./No

            state: Yes
                q: * (да/давай/конечно/пошли/идем) *
                a: Вы сказали "да", и оказались на третьем уровне вложенности.

            state: No
                q: * (нет/не) *
                a: Вы сказали "нет", но оказались на третьем уровне вложенности.

    state: BlockMix
        q!: *блоки*
        image: https://image.newsru.com/pict/id/large/1411555_20110923150259.gif
        a: Первый блок текста
        image: https://image.newsru.com/pict/id/large/1411555_20110923150259.gif
        buttons:
            "Списки" -> /Lists
            "Текст с кнопками" -> /TextAndButtons
        a: Второй блок текста
        buttons:
            "Таймаут" -> /Timeout
            "Стейт с вложенными стейтами" -> /Start

    state: TestHTML
        q!: html
        a: <b>Полужирный шрифт</b>, далее перенос строки<br><i>курсив</i>, далее перенос строки<br><strike>зачеркнутый текст</strike>, далее перенос строки<br><u>подчеркнутый текст</u>, далее перенос строки<br>номер телефона: +79628887766, далее перенос строки<br>
            <a href="https://295628.selcdn.ru/mybroker/tariffs/tariff_plan_trader.pdf" target="_blank">гиперссылка</a><br>
            Далее список маркированный:<br>
            <ul><li>бла</li>
            <li>бла</li>
            <li>бла</li></ul><br>
            Далее список нумерованный:<br>
            <ol><li>бла</li>
            <li>бла</li>
            <li>бла</li></ol>
        script:
            $response.replies[$response.replies.length - 1].markup = "html";
        buttons:
            {text: "Кнопка со ссылкой", url: "https://295628.selcdn.ru/mybroker/tariffs/tariff_plan_trader.pdf"}
            {text: "Да", transition: "./Да"}
            {text: "Нет", transition: "./Нет"}

        state: Да
            a: Вы нажали кнопку "Да"

        state: Нет
            a: Вы нажали кнопку "Нет"

    state: TestMarkdown
        q!: markdown
        a: **Полужирный шрифт**, далее перенос строки\n*курсив*, далее перенос строки\n~~зачеркнутый текст~~, далее перенос строки\n<u>подчеркнутый текст</u>, далее перенос строки\nномер телефона: +79628887766, далее перенос строки
            [гиперссылка](https://295628.selcdn.ru/mybroker/tariffs/tariff_plan_trader.pdf)\n
            Далее список маркированный:
            * бла
            * бла
            * бла
            Далее список нумерованный:
            1. бла
            2. бла
            3. бла
        buttons:
            {text: "Кнопка со ссылкой", url: "https://295628.selcdn.ru/mybroker/tariffs/tariff_plan_trader.pdf"}
            {text: "Да", transition: "./Да"}
            {text: "Нет", transition: "./Нет"}

        state: Да
            a: Вы нажали кнопку "Да"

        state: Нет
            a: Вы нажали кнопку "Нет"

    state: ImageResponse
        q!: *картинка*
        a: Далее идет картинка
        image: https://image.newsru.com/pict/id/large/1411555_20110923150259.gif

    state: Operator
        q!: *оператор*
        a: Перевод на оператора
        script:
            $response.replies = $response.replies || [];
            $response.replies.push({"type": "switch"});
            $analytics.setAutomationStatus(false);
            $jsapi.stopSession();

    state: FileEvent
        event!: fileEvent
        a: Вы прислали файл.
        buttons:
            "В меню" -> /Start

    state: Raw
        q!: *сырой*
        a: {{toPrettyString($request.rawRequest)}}

    state: Hello
        a: Привет привет

    state: Bye
        a: Пока пока

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}

    state: Match
        event!: match
        a: {{$context.intent.answer}}
