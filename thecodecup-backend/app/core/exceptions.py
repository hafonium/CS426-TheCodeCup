
class EmailAlreadyExistsException(Exception):
    def __init__(self, email: str):
        self.email = email
        self.message = f"Account with this email already exists"
        super().__init__(self.message)

class PasswordMismatchException(Exception):
    def __init__(self):
        self.message = "Old password is incorrect"
        super().__init__(self.message)

# Email exists but is not verified exception
class EmailVerificationException(Exception):
    def __init__(self):
        self.message = f"EMAIL_NOT_VERIFIED"
        super().__init__(self.message)