function getToken() {
    var res = $http.post("https://auth.tusvc.bcs.ru/auth/realms/perseus/protocol/openid-connect/token", {
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        form: {
            "client_id": "CraftTalkStageWeb",
            "username": "SVC-MicroSRV-chatapp",
            "password": "fHvmbQUNml9UQXbI853LiDQ2kxKxPj",
            "grant_type": "password",
            "client_secret": "P5wUzeupIqf2Lo0UaS3jIbgHIacBDHQu"
        }
    });
    if (res && res.isOk && res.data && res.data.access_token) return res.data.access_token;
    return null;
}

function finishSession(clientId) {
    var token = getToken();
    var res = $http.post("https://craft-talk.tusvc.bcs.ru/api/external/send-message/send-finish", {
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        body: {
            "CustomerId": "bcs-imported",
            "OmniUserId": clientId, //"ff571887-0d22-4004-a9da-a520c7e1976c", 
            "ChannelId": "channel_2a6b2d3",
            "Reason": "CLIENT_TIMEOUT"
        },
        timeout: 10000
    });
    if (res) return res;
}
