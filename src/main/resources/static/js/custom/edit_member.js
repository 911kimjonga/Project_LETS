function passwordValid(password) {
    return /^[a-zA-Z0-9!@#$%^&_-]{4,18}$/.test(password)
}

function nameValid(name) {
    return /^[가-힣A-Za-z]{2,10}$/.test(name)
}

function phoneNumberValid(phoneNumber) {
    return /[0-9]{10,11}$/.test(phoneNumber)
}

let imagePath = document.getElementById('imagePath');
let password = document.getElementById('password');
let name = document.getElementById('name');
let phoneNumber = document.getElementById('phoneNumber');
let gender = document.getElementById('gender');
let birthday = document.getElementById('birthday');

let passwordCheck = true;
let nameCheck = false;
let phoneNumberCheck = true;

password.addEventListener('change', () => {
    passwordCheck = passwordValid(password.value);
    let checkMessage = document.getElementById('passwordCheck');
    if (passwordCheck) {
        checkMessage.innerHTML = '';
        return passwordCheck;
    } else if (!passwordCheck) {
        checkMessage.innerHTML = '비밀번호 형식이 올바르지 않습니다.';
    }
})

name.addEventListener('change', () => {
    nameCheck = nameValid(name.value);
    let checkMessage = document.getElementById('nameCheck');
    if (nameCheck) {
        checkMessage.innerHTML = '';
        return nameCheck;
    } else if (!nameCheck) {
        checkMessage.innerHTML = '이름 형식이 올바르지 않습니다.';
    }
})

phoneNumber.addEventListener('change', () => {
    phoneNumberCheck = phoneNumberValid(phoneNumber.value);
    let checkMessage =document.getElementById('phoneNumberCheck');
    if (phoneNumberCheck) {
        checkMessage.innerHTML = '';
        return phoneNumberCheck;
    } else if (!phoneNumberCheck) {
        checkMessage.innerHTML = '전화번호 형식이 올바르지 않습니다.';
    }
})

document.getElementById('edit-form').addEventListener('submit', event => {
    event.preventDefault();

    if (name.value != null) {
        nameCheck = true;
    }

    if (passwordCheck && nameCheck && phoneNumberCheck) {

        const requestForm = new FormData();
        const editData = {
            password: password.value,
            name: name.value,
            phoneNumber: phoneNumber.value,
            gender: gender.value,
            birthday: birthday.value,
        };
        console.log(editData);
        console.log(editData);
        console.log(editData);
        const blob = new Blob([JSON.stringify(editData)], { type: 'application/json'});

        requestForm.append('editData', blob);
        requestForm.append('imagePath', imagePath.files[0]);

        fetch('member/edit', {
            method: 'POST',
            body: requestForm,
        }).then(response => {
            return response.text();
        }).then(message => {
            if (message === 'success') {
                alert('회원 정보가 정상적으로 수정되었습니다.');
                location.href = '/mypage';
            } else if (message === 'fail') {
                alert('회원 정보 수정이 실패하였습니다. 다시 시도해주세요.');
            }
        }).catch(error => {
            alert(error);
        })

    } else {
        if (name.value === null || name.value === undefined || name.value === '') {
            let checkMessage = document.getElementById('nameCheck');
            checkMessage.innerHTML = '이름을 입력하여 주십시오.';
        }
    }

})