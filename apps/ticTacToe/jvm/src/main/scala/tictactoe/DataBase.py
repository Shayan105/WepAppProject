import os

class DataBase:
    BASE = "C:/Users/Mouktar/Desktop/webapp-main/WepAppProject/driver/jvm/"

    @staticmethod
    def readFile(fileAddress: str) -> list[str]:
        with open(DataBase.BASE + fileAddress, 'r') as file:
            return file.read().splitlines()

    @staticmethod
    def appender(fileAddress: str, append: bool, toAppend: list[str]) -> None:
        mode = 'a' if append else 'w'
        with open(DataBase.BASE + fileAddress, mode) as file:
            for line in toAppend:
                file.write(line + '\n')

    @staticmethod
    def findLinePrefixed(fileAddress: str, prefix: str, beginsWith: bool) -> list[str]:
        lines = DataBase.readFile(fileAddress)
        filteredLines = [line for line in lines if (beginsWith and line.startswith(prefix)) or (not beginsWith and prefix in line)]
        return filteredLines if filteredLines else None

    @staticmethod
    def getDataLinePrefixed(fileAddress: str, prefix: str) -> list[str]:
        lines = DataBase.findLinePrefixed(fileAddress, prefix + ":", True)
        return [line[len(prefix) + 2:] for line in lines] if lines else None

    @staticmethod
    def replaceLine(fileAddress: str, prefix: str, newLine: str) -> None:
        lines = DataBase.readFile(fileAddress)
        if not any(prefix in line for line in lines):
            raise Exception(f"Prefix {prefix} not found in {fileAddress}")
        newData = [newLine if prefix in line else line for line in lines]
        DataBase.appender(fileAddress, False, newData)

    @staticmethod
    def replaceDataLine(fileAddress: str, prefix: str, newData: str) -> None:
        DataBase.replaceLine(fileAddress, prefix + ":", prefix + ": " + newData)

    @staticmethod
    def createFile(filePath: str) -> None:
        os.makedirs(os.path.dirname(DataBase.BASE + filePath), exist_ok=True)
        with open(DataBase.BASE + filePath, 'w') as file:
            pass

    @staticmethod
    def fileExists(fileAddress: str) -> bool:
        return os.path.exists(DataBase.BASE + fileAddress)
