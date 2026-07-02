
class EmailAlreadyExistsException(Exception):
    def __init__(self, email: str):
        self.email = email
        self.message = f"Account with this email exists"
        super().__init__(self.message)