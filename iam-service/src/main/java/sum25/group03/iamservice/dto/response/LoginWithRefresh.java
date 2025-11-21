package sum25.group03.iamservice.dto.response;

public record LoginWithRefresh(LoginResponse loginResponse, String refreshToken) {}