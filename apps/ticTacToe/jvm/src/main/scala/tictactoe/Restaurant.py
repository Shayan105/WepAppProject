class Restaurant:
    def __init__(self, name, description, rank, address):
        self.name = name
        self.description = description
        self.rank = rank
        self.address = address

    def __str__(self):
        return f"{self.name}#{self.description}#{self.rank}#{self.address}"

    def __eq__(self, other):
        if isinstance(other, Restaurant):
            return (self.name.strip() == other.name.strip() and
                    self.description.strip() == other.description.strip())
        return False


