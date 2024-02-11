from DataBase import DataBase
from Restaurant import Restaurant

class GrailleList:
    GRAILLE_LISTS_FOLDER_PATH = "graille_lists/"

    def __init__(self, name, collaborators, restaurantList):
        self.name = " "+name
        self.collaborators = collaborators
        self.restaurantList = restaurantList

    def pathFile(self):
        return f"{self.GRAILLE_LISTS_FOLDER_PATH}{hash(self)}.txt"

    def __hash__(self):
        return hash(self.name.strip())

    def create(self):
        if not DataBase.fileExists(self.pathFile()):
            DataBase.createFile(self.pathFile())
            lines = [
                f"name:{self.name}",
                f"collaborators:{','.join(str(hash(c)) for c in self.collaborators)}",
                f"restaurantList:{','.join(str(r) for r in self.restaurantList)}"
            ]
            DataBase.appender(self.pathFile(), True, lines)
        else:
            self.refresh()

    def refresh(self):
        retrieved = GrailleList.retrieveFromDb(hash(self))
        self.name = retrieved.name
        self.collaborators = retrieved.collaborators
        self.restaurantList = retrieved.restaurantList

    def addCollaborator(self, user):
        self.refresh()
        if user not in self.collaborators:
            self.collaborators.append(user)
            self.update("collaborators")
            otherUser = User.retriveFromDb(user)
            otherUser.grailleLists.append(hash(self))
            otherUser.update("grailleLists")

    def removeCollaborator(self, user):
        if user in self.collaborators:
            self.collaborators.remove(user)
            self.update("collaborators")
            otherUser = User.retriveFromDb(user)
            otherUser.grailleLists.remove(hash(self))
            otherUser.update("grailleLists")

    def addRestaurant(self, restaurant):
        if restaurant not in self.restaurantList:
            self.restaurantList.append(restaurant)
            self.update("restaurantList")
        else:
            print("Already present")

    def removeRestaurant(self, restaurant):
        if restaurant in self.restaurantList:
            self.restaurantList.remove(restaurant)
            self.update("restaurantList")

    def update(self, dataType):
        dataMap = {
            "name": self.name,
            "collaborators": ",".join(str(c) for c in self.collaborators),
            "restaurantList": ",".join(str(r) for r in self.restaurantList)
        }
        print(f"to write: {dataMap[dataType]}")
        DataBase.replaceDataLine(self.pathFile(), dataType, dataMap[dataType])

    def shareGrailleList(self, user):
        pass

    def getRestaurants(self):
        self.refresh()
        print(self.restaurantList)
        return self.restaurantList

    def getCollaborators(self):
        self.refresh()
        return self.collaborators


class GrailleListDB:
    GRAILLE_LISTS_FOLDER_PATH = "graille_lists/"

    @staticmethod
    def retrieveFromDb(id):
        pathFile = f"{GrailleListDB.GRAILLE_LISTS_FOLDER_PATH}{id}.txt"
        name = DataBase.getDataLinePrefixed(pathFile, "name")[0].strip()
        collaborators = [int(c.strip()) for c in DataBase.getDataLinePrefixed(pathFile, "collaborators")[0].split(",") if c]
        restaurantList = [
            Restaurant(*arr) for arr in (
                r.split("#") for r in DataBase.getDataLinePrefixed(pathFile, "restaurantList")[0].split(",")
            )
        ]
        return GrailleList(name, collaborators, restaurantList)


