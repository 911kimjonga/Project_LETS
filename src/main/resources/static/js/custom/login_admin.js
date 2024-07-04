function emailValid(email) {
<<<<<<< HEAD
    return /^[a-z0-9_-]{1,18}$/.test(email)
=======
    return /^[a-z0-9_-]$/.test(email)
>>>>>>> 03b3c70d72e6c7412828250db3e694d6a24bb4d6
}

function passwordValid(password) {
    return /^[a-z0-9_-]{4,18}$/.test(password)
}

const email = document.querySelector('#email');
const password = document.querySelector('#password');
const remember = document.querySelector('#remember');

let emailCheck = false;
let passwordCheck = false;

email.addEventListener('change', () => {
    emailCheck = emailValid(document.querySelector('#email').value);
    let checkMessage = document.querySelector('#emailCheck');
    if (emailCheck) {
        checkMessage.innerHTML = '';
        return emailCheck;
    } else if (!emailCheck) {
<<<<<<< HEAD
        checkMessage.innerHTML = '아이디 형식이 올바르지 않습니다.';
=======
        checkMessage.innerHTML = '이메일 형식이 올바르지 않습니다.';
>>>>>>> 03b3c70d72e6c7412828250db3e694d6a24bb4d6
    }
})

password.addEventListener('change', () => {
    passwordCheck = passwordValid(document.querySelector('#password').value);
    let checkMessage = document.querySelector('#passwordCheck');
    if (passwordCheck) {
        checkMessage.innerHTML = '';
        return passwordCheck;
    } else if (!passwordCheck) {
        checkMessage.innerHTML = '비밀번호 형식이 올바르지 않습니다.';
    }
})

document.querySelector('form#login_form').addEventListener('submit', event => {
    event.preventDefault();

    let checkMessageToEmail = document.querySelector('#emailCheck');
    let checkMessageToPassword = document.querySelector('#passwordCheck');
    if (email.value != null) {
        if (emailValid(email.value)) {
<<<<<<< HEAD
            checkMessageToEmail.innerHTML = '';
            emailCheck = true;
        } else {
            checkMessageToEmail.innerHTML = '아이디 형식이 올바르지 않습니다.';
=======
            emailCheck = true;
        } else {
            checkMessageToEmail.innerHTML = '이메일 형식이 올바르지 않습니다.';
>>>>>>> 03b3c70d72e6c7412828250db3e694d6a24bb4d6
        }
    }
    if (password.value != null) {
        if (passwordValid(password.value)) {
<<<<<<< HEAD
            checkMessageToPassword.innerHTML = '';
=======
>>>>>>> 03b3c70d72e6c7412828250db3e694d6a24bb4d6
            passwordCheck = true;
        } else {
            checkMessageToPassword.innerHTML = '비밀번호 형식이 올바르지 않습니다.';
        }
    }

<<<<<<< HEAD
    if ((emailCheck && email.value != null) && passwordCheck) {

        fetch('/admin/login', {
=======
    if ((emailCheck || email.value != null) && passwordCheck) {

        fetch('/member/login', {
>>>>>>> 03b3c70d72e6c7412828250db3e694d6a24bb4d6
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: email.value,
                password: password.value,
            }),
        }).then(response => {
            return response.text();
        }).then(message => {
            if (message === 'success') {
                if (remember.checked) {
                    setCookie("remember", email.value, 2592000)
                } else {
                    setCookie("remember", email.value, 0);
                }
                location.href = "/";    // 메인페이지로
            } else if (message === 'fail') {
                alert('아이디 또는 비밀번호가 일치하지 않습니다.');
            }
        }).catch(error => {
            alert(error);
        })
    } else {
        document.querySelectorAll('.form-control').forEach((e, i) => {
            if (e.value === null || e.value === undefined || e.value === '') {
                let checkMessage = document.querySelectorAll('.loginCheck');
                switch (checkMessage[i].getAttribute('id')) {
                    case 'emailCheck':
                        checkMessage[i].innerHTML = '이메일을 입력하세요';
                        break;
                    case 'passwordCheck':
                        checkMessage[i].innerHTML = '비밀번호을 입력하세요';
                        break;
                }

            }
        })
    }

})

function setCookie(cookieName, value, exdays) {
    var exdate = new Date();
    exdate.setDate(exdate.getDate() + exdays);
    var cookieValue = escape(value) + ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
    document.cookie = cookieName + "=" + cookieValue + "; path=/;";
<<<<<<< HEAD
}
=======
}
>>>>>>> 03b3c70d72e6c7412828250db3e694d6a24bb4d6
