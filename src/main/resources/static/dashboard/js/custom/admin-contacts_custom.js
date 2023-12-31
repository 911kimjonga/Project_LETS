const url = '/admin/contact';

const contacts = document.querySelectorAll('li.contacts');
contacts.forEach((e, i) => {
    const approveBtn = e.querySelector('button.approve_btn');
    const refuseBtn = e.querySelector('button.refuse_btn');
    const contactId = contactList[i].id;

    if (approveBtn) {
        approveBtn.addEventListener('click', event => {
            event.preventDefault();

            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(contactId),
            }).then(response => {
                return response.text();
            }).then(message => {
                if (message === 'success') {
                    alert('입점 신청 승인이 완료되었습니다.');
                    location.href = url;
                } else if (message === 'fail') {
                    alert('입점 신청 승인이 정상적으로 진행되지 않았습니다. 다시 시도해주세요.');
                }
            }).catch(error => {
                alert(error);
            })
        })
    }

    if (refuseBtn) {
        refuseBtn.addEventListener('click', event => {
            event.preventDefault();

            fetch(url, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(contactId),
            }).then(response => {
                return response.text();
            }).then(message => {
                if (message === 'success') {
                    alert('입점 신청 거부가 완료되었습니다.');
                    location.href = url;
                } else if (message === 'fail') {
                    alert('입점 신청 거부가 정상적으로 진행되지 않았습니다. 다시 시도해주세요.');
                }
            }).catch(error => {
                alert(error);
            })
        })
    }

})