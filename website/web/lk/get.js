function saveToken(token) {
    localStorage.setItem('tokenData', JSON.stringify(token));
}

function refreshToken(token) {
    return fetch('http://localhost:8080/archery/auth/refreshToken', {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({token}),
    })
        .then((res) => {
            if (res.status === 200) {
                const tokenData = res.json(); //не уверена что так
                saveToken(JSON.stringify(tokenData));
                return Promise.resolve();
            }
            return Promise.reject();
        });
}

// export
async function fetchWithAuth(url, options) {
    const loginUrl = '../login/login.html';
    var tokenData_t = null;
    var access_t = null;
    var expires = null;
    if (localStorage.tokenData) {
        // console.log("token exist");
        tokenData_t = localStorage.tokenData;
        // console.log(localStorage.tokenData);
        access_t = JSON.parse(localStorage.getItem('tokenData'));
        // console.log(access_t);
        expires = JSON.parse(localStorage.getItem('expiresIn'));
        // console.log(expires);

    } else {
        console.log("localStorage.authToken = null");
        return window.location.replace(loginUrl);
    }

    if (!options.headers) {
        console.log("there aren't headers");
        options.headers = {}; // Нужно добавить необходимые заголовки
    }

    if (tokenData_t) {
        // console.log("tokenData exist");
        if (Date.now() >= expires) {
            console.log("token isn't valid");
            try {
                const newToken = await refreshToken(access_t);
                saveToken(newToken);
            } catch (err) {
                return window.location.replace(loginUrl);
            }
        }
        options.headers.Authorization = `Bearer ${access_t}`;
    }
    return fetch(url, options);
}

function getData(url, field_id) {
    fetchWithAuth(url,
        {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(response => {
        return response.text();
    }).then(data => {
        document.getElementById(field_id).value = data;
        // console.log(data);
    }).catch(err => {
        // alert("Fail");
        console.log(err);
    });
}

getData("http://localhost:8080/archery/profile/getFirstName", 'inputFirstname');
getData("http://localhost:8080/archery/profile/getLastName", 'inputLastName');
getData("http://localhost:8080/archery/profile/getEmail", 'inputEmailAddress');
getData("http://localhost:8080/archery/profile/getPhoneNumber", 'inputPhone');
getData("http://localhost:8080/archery/profile/getBirthDate", 'inputBirthday');
getData("http://localhost:8080/archery/profile/getAttendedClasses", 'attendedClasses');

function getTicket() {
    fetchWithAuth("http://localhost:8080/archery/profile/getTicket",
        {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(response => {
        return response.json();
    }).then(data => {
        if (data.ticketType == "long limit") {
            document.getElementById('ticket').value = "На 8 занятий";
        } else if (data.ticketType == "short limit") {
            document.getElementById('ticket').value = "На 4 занятий";
        } else if (data.ticketType == "unlimited") {
            document.getElementById('ticket').value = "Безлимитный";
        } else if (data.ticketType == "") {
            document.getElementById('ticket').value = "Нет";
        }
        // console.log(data);
    }).catch(err => {
        // alert("Fail");
        console.log(err);
    });
}

getTicket();