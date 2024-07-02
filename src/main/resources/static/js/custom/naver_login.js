var naver_id_login = new naver_id_login("qI0lCLf5is8Pnnk1Oj8P", "http://localhost:80/naverCallback");
var state = naver_id_login.getUniqState();

naver_id_login.setButton("green", 3,40);
naver_id_login.setDomain("http://localhost:80/");
naver_id_login.setState(state);
naver_id_login.setPopup();
naver_id_login.init_naver_id_login();

// 네이버 사용자 프로필 조회 이후 프로필 정보를 처리할 callback function
function naverSignInCallback() {
    let email = naver_id_login.getProfileData('email');
    let name = naver_id_login.getProfileData('name');

    // Fetch API를 사용하여 서버로 데이터 전송
    sendDataToServer(email, name);
}

// 네이버 사용자 프로필 조회
naver_id_login.get_naver_userprofile("naverSignInCallback()");

function sendDataToServer(email, name) {

    $.ajax({
        type: 'POST',
        url: '/member/naver',
        data: {
            email: email,
            name: name
        },
        success: function(response) {
            if (response === 'success') {
                // 부모 창의 함수 호출
                window.close();
                window.opener.handleLoginSuccess();
            } else if (response === 'fail') {
                alert('로그인이 실패하였습니다.');
            }
        },
        error: function(error) {
            alert('오류 발생: ' + error);
        }
    });
}