# SudaBot0222_share

이 코드는 카카오가 제공하는 KoGPT 을 호출해서 챗봇을 만들어 보았습니다. 

쿼터가 있어서 월 1000건이 넘어가면 응답을 안 할수도 있습니다.

그냥 코드 구현에 관한 미흡하지만 보시는 데 도움이 되시길 바랄께요.

This code calls KoGPT provided by Kakao to create a chatbot.

There is a quota, so if you exceed 1000 per month, you may not be able to respond.

It's just a lack of code implementation, but I hope it helps you see.


local.properties 파일에 아래 4줄이 추가 되어야 합니다.

KAKAO_API_KEY=카카오 개발자 계정에서 받은 Netive API 키
KAKAO_REST_KEY=카카오 개발자 계정에서 받은 REST API 키
ADMOB_APP_ID= 구글 admob 앱 ID
ADMOB_BANNER_ID= 구글 admob banner 광고 ID
