import math
import time
import pygame
from pygame.locals import *

#defining global variables/tuples here:
G = 6.67408 * (10**-11)

BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
GREY = (200, 200, 200)
RED = (255, 000, 000) 
ORANGE = (255, 165, 000)


class Mass:

    def __init__(self, name, mass, radius, stationary, orbital_radius):
        #characteristics about the mass
        self.name = name
        self.mass = mass
        self.radius = radius
        self.stationary = stationary
        self.orbital_radius = orbital_radius

        self.xpos = 0
        self.ypos = 0
        self.xvelo = 0
        self.yvelo = 0

    def __repr__(self):
        return self.name
    
    def gravitational_influence(self, time_interval, barrycenter):
        deltax = self.xpos - barrycenter.xpos
        deltay = self.ypos - barrycenter.ypos
        distance = math.sqrt((deltax**2) + (deltay**2))

        #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
        force_gravity = (G*self.mass*barrycenter.mass) / (distance**2)
        #angles drawn from the space craft
        theta = math.atan2(deltay, deltax)

        net_xforce = force_gravity * -math.cos(theta)
        net_yforce = force_gravity * -math.sin(theta)

        net_xaccel = net_xforce / self.mass
        net_yaccel = net_yforce / self.mass

        #calculates the change in velocity over a given time step interval
        self.xvelo += net_xaccel * time_interval
        self.yvelo += net_yaccel * time_interval	

    def deltaPosition(self, time_interval):
        #determines the change in position over the same time step interval
        self.xpos += self.xvelo * time_interval
        self.ypos += self.yvelo * time_interval


class SpaceCraft:

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

        for body in body_list:
            deltax = self.xpos - body.xpos
            deltay = self.ypos - body.ypos
            distance = math.sqrt((deltax**2) + (deltay**2))

            #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
            force_gravity = (G*self.mass*body.mass) / (distance**2)
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
#name, mass, radius, stationary, orbital_radius
earth = Mass("Earth", 5.97*(10**24), 6378, False, 149.6*(10**6))
sun = Mass("Sun", 1.99*(10**30), 6.96*(10**5), True, 0)

body_list = (earth, sun)

starship = SpaceCraft("starship", 10, 1, (149.6*(10**6)+35785), 0, 0, 3070)



#parameters: display dimensions, simulation width included in the display window, iteration pause interval
def main(screen_width, screen_height, simulation_width, iteration_pause):

    #display infomation and dimensions
    DP_WIDTH, DP_HEIGHT = screen_width, screen_height    
    REAL_WIDTH = simulation_width #raw width of the display in km
    SCALE_FACTOR = round(REAL_WIDTH / DP_WIDTH) #ratio of km:1 pixel
    TARGET = starship #either a class object or x-y coordinates

    #starship will always have the 0 index
    target_list = [starship]
    for body in body_list:
        target_list.append(body)
    
    barrycenter = None
    for body in body_list:
        if body.stationary == True:
            barrycenter = body
            body.xpos, body.ypos = 0, 0
            body.xvelo, body.xvelo = 0, 0
            break
    
    for body in body_list:
        if body != barrycenter:
            body.xpos = body.orbital_radius + barrycenter.xpos
            body.ypos = barrycenter.ypos
            body.xvelo = 0
            body.yvelo = math.sqrt(G*barrycenter.mass*(1/body.orbital_radius)) 

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
                    SCALE_FACTOR = round(0.5 * SCALE_FACTOR)
                if event.key == pygame.K_2:
                    SCALE_FACTOR = round(2 * SCALE_FACTOR)

                if event.key == pygame.K_q:
                    if target_list.index(TARGET)+1 == len(target_list):
                        TARGET = target_list[0]
                    else:
                        TARGET = target_list[target_list.index(TARGET)+1]
                if event.key == pygame.K_e:
                    if target_list.index(TARGET)+1 == len(target_list):
                        TARGET = target_list[0]
                    else:
                        TARGET = target_list[target_list.index(TARGET)-1]   


        #calculate gravity's influence on each orbiting body
        for body in body_list:
            if body != barrycenter:
                body.gravitational_influence(0.01, barrycenter)
                body.deltaPosition(0.01)

        starship.gravitational_influence(0.01)
        starship.deltaPosition(0.01)
        
        DP_CENTER_X = DP_WIDTH/2 + (-1 * scale_converter(TARGET.xpos, SCALE_FACTOR))
        DP_CENTER_Y = DP_HEIGHT/2 + (-1 * scale_converter(TARGET.ypos, SCALE_FACTOR))

        screen.fill(BLACK)
        #draw planets
        for body in body_list:
            dp_position = [DP_CENTER_X + scale_converter(body.xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(body.ypos, SCALE_FACTOR)]
            #draw.circle(surface, color, center, radius)
            pygame.draw.circle(screen, WHITE, dp_position, scale_converter(body.radius, SCALE_FACTOR))
            pygame.draw.polygon(screen, ORANGE, [dp_position, [dp_position[0]+5, dp_position[1]-10], [dp_position[0]-5, dp_position[1]-10]])
        
        #draw craft
        craft_dp_position = [DP_CENTER_X + scale_converter(starship.xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(starship.ypos, SCALE_FACTOR)]
        #using an inverse tangent function allows the starship to scale according to two asymptotes
        craft_dp_radius = -20*math.atan(0.1*SCALE_FACTOR - 12) + 35
        pygame.draw.circle(screen, GREY, craft_dp_position, starship.radius)
        #pygame.draw.polygon(screen, RED, [craft_dp_position, [craft_dp_position[0]+5, craft_dp_position[1]-10], [craft_dp_position[0]-5, craft_dp_position[1]-10]])
        
        pygame.display.set_caption(str(i))
        pygame.display.update()
        time.sleep(iteration_pause)

#parameters: real width included in the display window, iteration pause interval
main(1000, 1000, 100000, 0.01)