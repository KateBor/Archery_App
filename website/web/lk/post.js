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
        console.log(expires);

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


function save_changes() {
    var firstname = document.getElementById('inputFirstname').value;
    var lastName = document.getElementById('inputLastName').value;
    //var emailAddress = document.getElementById('inputEmailAddress').value;
    var phone = document.getElementById('inputPhone').value;
    var birth_date = document.getElementById('inputBirthday').value;
    var url = "http://localhost:8080/archery/profile/updateAll";
    // console.log(firstname);
    // console.log(lastName);
    // console.log(emailAddress);
    // console.log(phone);
    // console.log(birth_date);
    // window.alert(firstname);
    // window.alert(lastName);
    // window.alert(emailAddress);

    var data = {
        first_name: firstname,
        last_name: lastName,
        phone_number: phone,
        //email: emailAddress,
        birth_date: birth_date
    };
    // var result =
    fetchWithAuth(url,
        {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
        }).then((res) => {
        if (res.status === 200) {
            window.alert("success");
        } else {
            window.alert("fail");
        }
    });
    window.alert("saving...");
    // let result = response.json();
    // alert(result.message);
}


