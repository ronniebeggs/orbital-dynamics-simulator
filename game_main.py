import math
import time
import pygame
from pygame.locals import *

#defining global variables/tuples here:
G = 6.67408 * (10**-11)

BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
RED = (255, 000, 000) 


class Mass:

    def __init__(self, name, mass, radius, init_xpos=0, init_ypos=0, init_xvelo=0, init_yvelo=0):
        self.name = name
        self.mass = mass
        self.radius = radius
        self.xpos = init_xpos
        self.ypos = init_ypos
        self.xvelo = init_xvelo
        self.yvelo = init_yvelo

    def __repr__(self):
        return self.name

    def gravitational_influence(self, time_interval):
        #vectors reffered in [magnitude, angle] pairs
        #all angles drawn from the space craft
        gravity_vectors = []

        for planet in planet_list:
            if planet == self:
                continue #prevents planets from calculating gravity in relation to themselves
            deltax = self.xpos - planet.xpos
            deltay = self.ypos - planet.ypos
            distance = math.sqrt((deltax**2) + (deltay**2))

            #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
            force_gravity = (G*self.mass*planet.mass) / (distance**2)
            theta = math.atan2(deltay, deltax)

            gravity_vectors.append([force_gravity, theta])

        net_xforce, net_yforce = 0, 0
        for vector in gravity_vectors:
            
            net_xforce += vector[0] * -math.cos(vector[1])
            net_yforce += vector[0] * -math.sin(vector[1])

        net_xaccel = net_xforce / self.mass
        net_yaccel = net_yforce / self.mass

        #calculates the change in velocity over a given time step interval
        self.xvelo += net_xaccel * time_interval
        self.yvelo += net_yaccel * time_interval	


    def deltaPosition(self, time_interval):
        #determines the change in position over the same time step interval
        self.xpos += self.xvelo * time_interval
        self.ypos += self.yvelo * time_interval


def scale_converter(real_distance, SCALE_FACTOR):

    scaled_distance = real_distance / SCALE_FACTOR
    return round(scaled_distance)


#initialize each planetary body
#name, mass, radius, init_xpos=0, init_ypos=0, init_xvelo=0, init_yvelo=0
kerbol = Mass("kerbol", 5.972*(10**24), 6378, 0, 0, 0, 0)

planet_list = [kerbol]

starship = Mass("starship", 10, 1, 179828, 0, 0, -47078)



#parameters: display dimensions, simulation width included in the display window, iteration pause interval
def main(screen_width, screen_height, simulation_width, iteration_pause):

    #display infomation and dimensions
    DP_WIDTH, DP_HEIGHT = screen_width, screen_height    
    REAL_WIDTH = simulation_width #raw width of the display in km
    SCALE_FACTOR = round(REAL_WIDTH / DP_WIDTH) #ratio of km:1 pixel
    TARGET = starship #either a class object or x-y coordinates
    
    #initializes pygame library tools and display window
    pygame.init()
    screen = pygame.display.set_mode((DP_WIDTH, DP_HEIGHT))
    
    i = 0 #iteration counter to help understand the speed of each iteration
    #event loop
    running = True
    while running:
        i += 1

        #pygame.event list keeps track of all keypress/mouseclicks that occur
        for event in pygame.event.get():
            
            #closes window when exit button pressed
            if event.type == pygame.QUIT:
                running = False

            #zoom in or out using up and down keys, respectively
            if event.type == pygame.KEYDOWN:
                
                if event.key == pygame.K_1:
                    SCALE_FACTOR = round(0.9 * SCALE_FACTOR)
                if event.key == pygame.K_2:
                    SCALE_FACTOR = round(1.1 * SCALE_FACTOR)


        #calculate gravity's influence on each orbiting body
        for planet in planet_list:
            planet.gravitational_influence(0.01)
            planet.deltaPosition(0.01)

        starship.gravitational_influence(0.01)
        starship.deltaPosition(0.01)
        
        DP_CENTER_X = DP_WIDTH/2 + (-1 * scale_converter(TARGET.xpos, SCALE_FACTOR))
        DP_CENTER_Y = DP_HEIGHT/2 + (-1 * scale_converter(TARGET.ypos, SCALE_FACTOR))

        screen.fill(BLACK)
        #draw planets
        for planet in planet_list:
            planet_dp_position = [DP_CENTER_X + scale_converter(planet.xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(planet.ypos, SCALE_FACTOR)]
            #draw.circle(surface, color, center, radius)
            pygame.draw.circle(screen, WHITE, planet_dp_position, scale_converter(planet.radius, SCALE_FACTOR))

        #draw craft
        craft_dp_position = [DP_CENTER_X + scale_converter(starship.xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(starship.ypos, SCALE_FACTOR)]
        #using an inverse tangent function allows the starship to scale according to two asymptotes
        craft_dp_radius = -20*math.atan(0.1*SCALE_FACTOR - 12) + 35
        pygame.draw.circle(screen, RED, craft_dp_position, craft_dp_radius)
        
        pygame.display.set_caption(str(i))
        pygame.display.update()
        time.sleep(iteration_pause)

#parameters: real width included in the display window, iteration pause interval
main(1000, 1000, 400000, 0.01)