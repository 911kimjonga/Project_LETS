function emailValid(email) {
    return /^[A-Za-z0-9.\-_]+@([A-Za-z0-9-]+\.)+[A-Za-z]{2,6}$/.test(email)
}

function passwordValid(password) {
    return /^[a-zA-Z0-9!@#$%^&_-]{4,18}$/.test(password)
}

function nameValid(name) {
    return /^[가-힣]{2,10}$/.test(name)
}

const email = document.querySelector('#email');
const password = document.querySelector('#password');
const passwordConfirm = document.querySelector('#passwordConfirm');
const name = document.querySelector('#name');

let emailCheck = false;
let passwordCheck = false;
let passwordConfirmCheck = false;
let nameCheck = false;

email.addEventListener('change', () => {
    emailCheck = emailValid(document.querySelector('#email').value);
    let checkMessage = document.querySelector('#emailCheck');
    if (emailCheck) {
        checkMessage.innerHTML = '';
        return emailCheck;
    } else if (!emailCheck) {
        checkMessage.innerHTML = '이메일 형식이 올바르지 않습니다.';
    }
})

password.addEventListener('change', () => {
    passwordCheck = passwordValid(document.querySelector('#password').value);
    let checkMessage = document.querySelector('#passwordCheck');

    if (!(password.value === passwordConfirm.value)) {
        document.querySelector('#passwordConfirmCheck').innerHTML = '비밀번호가 일치하지 않습니다.';
    }

    if (passwordCheck) {
        checkMessage.innerHTML = '';
        return passwordCheck;
    } else if (!passwordCheck) {
        checkMessage.innerHTML = '비밀번호 형식이 올바르지 않습니다.';
    }
})

passwordConfirm.addEventListener('change', () => {
    passwordConfirmCheck = (passwordConfirm.value === password.value);
    let checkMessage = document.querySelector('#passwordConfirmCheck');
    if (passwordConfirmCheck) {
        checkMessage.innerHTML = '';
        return passwordConfirmCheck;
    } else if (!passwordConfirmCheck) {
        checkMessage.innerHTML = '비밀번호가 일치하지 않습니다.';
    }
})

name.addEventListener('change', () => {
    nameCheck = nameValid(document.querySelector('#name').value);
    let checkMessage = document.querySelector('#nameCheck');
    if (nameCheck) {
        checkMessage.innerHTML = '';
        return nameCheck;
    } else if (!nameCheck) {
        checkMessage.innerHTML = '이름 형식이 올바르지 않습니다.';
    }
})

document.querySelector('form#register_form').addEventListener('submit', event => {
    event.preventDefault();

    if (emailCheck && passwordCheck && passwordConfirmCheck && nameCheck) {
        const registerForm = {
            email: email.value,
            password: password.value,
            name: name.value
        }
        fetch('/member/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(registerForm),
        }).then(response => {
            return response.text();
        }).then(message => {
            if (message === 'success') {
                alert("회원가입이 완료되었습니다.");
                location.href = '/member/login';
            } else if (message === 'duplicate') {
                alert("해당 이메일은 이미 사용중인 이메일 입니다.");
            } else if (message === 'fail') {
                alert('회원가입이 정상적으로 진행되지 않았습니다. 다시 시도해주세요.');
            }
        }).catch(error => {
            alert(error);
        })
    } else {
        document.querySelectorAll('.form-control').forEach((e, i) => {
            if (e.value === null || e.value === undefined || e.value === '') {
                let checkMessage = document.querySelectorAll('.registerCheck');
                switch (checkMessage[i].getAttribute('id')) {
                    case 'emailCheck':
                        checkMessage[i].innerHTML = '이메일을 입력하세요';
                        break;
                    case 'passwordCheck':
                        checkMessage[i].innerHTML = '비밀번호을 입력하세요';
                        break;
                    case 'passwordConfirmCheck':
                        checkMessage[i].innerHTML = '비밀번호 확인을 입력하세요';
                        break;
                    case 'nameCheck':
                        checkMessage[i].innerHTML = '이름을 입력하세요';
                        break;
                }

            }
        })
    }

})