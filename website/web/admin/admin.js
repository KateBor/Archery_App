function getRole() {
    let jwt = JSON.parse(localStorage.getItem('tokenData'));
    let jwtData = jwt.split('.')[1]
    let decodedJwtJsonData = window.atob(jwtData)
    let decodedJwtData = JSON.parse(decodedJwtJsonData)

    let role = decodedJwtData.roles
    console.log('jwtData: ' + jwtData)
    console.log('decodedJwtJsonData: ' + decodedJwtJsonData)
    console.log('decodedJwtData: ' + decodedJwtData)
    console.log('role: ' + role)
    return role
}

function lk() {
    let role = getRole();
    if (role == 'ROLE_ADMIN') {     // нестрогое сравнение, по-другому хз
        console.log("ROLE_ADMIN");
        document.location.href = 'admin/admin_lk.html';
    } else if (role == 'ROLE_USER') {
        console.log("ROLE_USER");
        document.location.href = 'lk/lk.html';
    } else {
        document.location.href = 'login/login.html';
    }
    // };
}

// if (getRole() != 'ROLE_ADMIN') {
//     document.location.href = '../login/login.html';
// }
