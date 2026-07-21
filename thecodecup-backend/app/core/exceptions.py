
class EmailAlreadyExistsException(Exception):
    def __init__(self, email: str):
        self.email = email
        self.message = f"Account with this email already exists"
        super().__init__(self.message)

class PasswordMismatchException(Exception):
    def __init__(self):
        self.message = "Old password is incorrect"
        super().__init__(self.message)