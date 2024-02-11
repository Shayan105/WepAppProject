from DataBase import DataBase
from GrailleList import GrailleList
class User:
    USERS_FOLDER_PATH = "users/"

    def __init__(self, name, icon, logs, friendList, grailleLists):
        self.name = name
        self.icon = icon
        self.logs = logs
        self.friendList = friendList
        self.grailleLists = grailleLists
        self.pathFile = f"{User.USERS_FOLDER_PATH}{hash(self)}.txt"

    def getFriendList(self):
        l = GrailleList.retrieveFromDb(hash(self)).friendList
        self.friendList = l
        return self.friendList

    def getGrailleLists(self):
        self.refresh()
        return self.grailleLists

    def refresh(self):
        retrieved = User.retriveFromDb(hash(self))
        self.name = retrieved.name
        self.logs = retrieved.logs
        self.friendList = retrieved.friendList
        self.grailleLists = retrieved.grailleLists

    def create(self):
        exists = DataBase.fileExists(self.pathFile)
        if exists:
            self.refresh()
        else:
            DataBase.createFile(self.pathFile)
            with DataBase.appender(self.pathFile, True) as appender:
                listData = [
                    f"name: {self.name}",
                    f"icon: {self.icon}",
                    f"logs: {self.logs[0]},{self.logs[1]}",
                    f"friendList: {','.join(map(str, self.friendList))}",
                    f"grailleLists: {','.join(str(g) for g in self.grailleLists)}"
                ]
                appender(listData)

    def addFriend(self, friend):
        if friend not in self.friendList:
            self.friendList.append(friend)
            otherUser = User.retriveFromDb(friend)
            otherUser.friendList.append(hash(self))
            otherUser.update("friendList")
            self.update("friendList")
        else:
            print("already friends")

    def addGrailleList(self, graillelists):
        if isinstance(graillelists, GrailleList):
            self.grailleLists.append(hash(graillelists))
        else:
            self.grailleLists.append(graillelists)
        self.update("grailleLists")

    def removeFriend(self, friend):
        if friend in self.friendList:
            otherUser = User.retriveFromDb(friend)
            otherUser.friendList = [x for x in otherUser.friendList if x != friend]
            otherUser.update("friendList")
            self.friendList = [x for x in self.friendList if x != friend]
            self.update("friendList")
        else:
            print(f"Not currently friends with {friend}")

    def update(self, dataType):
        lineData = {
            "name": self.name,
            "icon": self.icon,
            "logs": f"{self.logs[0]},{self.logs[1]}",
            "friendList": ','.join(map(str, self.friendList)),
            "grailleLists": ','.join(str(g) for g in self.grailleLists)
        }.get(dataType, "")
        print(f"{self.name} updating {dataType} with path {self.pathFile} where hashcode is {hash(self)}")
        DataBase.replaceDataLine(self.pathFile, dataType, lineData)

    def __hash__(self):
        return hash(self.name)


def retriveFromDb(userId):
    pathFile = f"{User.USERS_FOLDER_PATH}{userId}.txt"
    name = DataBase.getDataLinePrefixed(pathFile, "name")[0].strip()
    icon = DataBase.getDataLinePrefixed(pathFile, "icon")[0]
    logs = DataBase.getDataLinePrefixed(pathFile, "logs")[0].split(",")
    friendList = [int(i.strip()) for i in DataBase.getDataLinePrefixed(pathFile, "friendList")[0].split(",") if i.strip()]
    grailleLists = [int(i.strip()) for i in DataBase.getDataLinePrefixed(pathFile, "grailleLists")[0].split(",") if i.strip()]
    return User(name, icon, (logs[0], logs[1]), friendList, grailleLists)


